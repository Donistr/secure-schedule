package org.example.client.service.impl;

import org.example.client.service.QueueTaskSchedulerService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Scope("prototype")
public class QueueTaskSchedulerServiceImpl implements QueueTaskSchedulerService {

    private static final ZoneId TIME_ZONE = ZoneId.systemDefault();

    private static final long CLEANUP_TIME_TO_ORDER_MAP_DELAY_MILLIS = 1500;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    private final Set<Future<?>> scheduledFutures = ConcurrentHashMap.newKeySet();

    private final ConcurrentMap<Long, AtomicLong> timeToOrder = new ConcurrentHashMap<>();

    @Override
    public void scheduleTaskAt(LocalDateTime dateTime, Runnable task) {
        long nowMillis = LocalDateTime.now().atZone(TIME_ZONE).toInstant().toEpochMilli();
        long targetMillis = dateTime.atZone(TIME_ZONE).toInstant().toEpochMilli();

        long delayMillis = targetMillis - nowMillis;
        if (delayMillis < 0) {
            delayMillis = 0;
        }

        AtomicLong orderCounter = timeToOrder.computeIfAbsent(targetMillis, k -> new AtomicLong(0));
        if (orderCounter.longValue() == 0) {
            scheduler.schedule(() ->
                            timeToOrder.remove(targetMillis),
                    delayMillis + CLEANUP_TIME_TO_ORDER_MAP_DELAY_MILLIS,
                    TimeUnit.MILLISECONDS
            );
        }

        delayMillis += orderCounter.getAndIncrement();

        CompletableFuture<Void> future = CompletableFuture.runAsync(
                () -> {
                    try {
                        task.run();
                    } catch (Throwable ignored) {
                        Thread.currentThread().interrupt();
                    }
                },
                CompletableFuture.delayedExecutor(delayMillis, TimeUnit.MILLISECONDS, scheduler)
        );
        future.thenRun(() -> scheduledFutures.remove(future));

        scheduledFutures.add(future);
    }

    @Override
    public void cancelAllTasks() {
        scheduledFutures.forEach(f -> f.cancel(true));
        scheduledFutures.clear();
        timeToOrder.clear();
    }

}
