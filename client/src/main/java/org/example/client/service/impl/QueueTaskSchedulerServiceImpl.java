package org.example.client.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.SneakyThrows;
import org.example.client.service.QueueTaskSchedulerService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Scope("prototype")
public class QueueTaskSchedulerServiceImpl implements QueueTaskSchedulerService {

    private record ScheduledTask(
            long triggerTimeMillis,
            long sequenceId,
            Runnable runnable
    ) implements Delayed {

        @Override
        public long getDelay(TimeUnit unit) {
            long now = System.currentTimeMillis();
            return unit.convert(Math.max(0, triggerTimeMillis - now), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            ScheduledTask other = (ScheduledTask) o;
            int timeCmp = Long.compare(this.triggerTimeMillis, other.triggerTimeMillis);
            if (timeCmp != 0) {
                return timeCmp;
            }
            return Long.compare(this.sequenceId, other.sequenceId);
        }

    }

    private static final ZoneId TIME_ZONE = ZoneId.systemDefault();

    private final AtomicLong sequence = new AtomicLong(0);

    private final DelayQueue<ScheduledTask> delayQueue = new DelayQueue<>();

    private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    private volatile Future<?> currentFuture;

    private boolean running = true;

    @PostConstruct
    private void init() {
        startLoop();
    }

    @PreDestroy
    private void destroy() {
        running = false;
        cancelAllTasks();
        worker.shutdownNow();
    }

    @Override
    public void scheduleTaskAt(LocalDateTime dateTime, Runnable task) {
        long targetMillis = dateTime.atZone(TIME_ZONE).toInstant().toEpochMilli();
        long sequenceId = sequence.getAndIncrement();

        ScheduledTask scheduledTask = new ScheduledTask(targetMillis, sequenceId, task);
        delayQueue.offer(scheduledTask);
    }

    @Override
    public void cancelAllTasks() {
        delayQueue.clear();
        currentFuture.cancel(true);
        sequence.set(0);
        startLoop();
    }

    @SneakyThrows
    private void runLoop() {
        while (running) {
            ScheduledTask task = delayQueue.take();
            task.runnable.run();
        }
    }

    private void startLoop() {
        currentFuture = worker.submit(this::runLoop);
    }

}
