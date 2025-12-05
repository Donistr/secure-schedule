package org.example.client.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.example.client.service.QueueTaskSchedulerService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
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

    private Thread workerThread;

    private boolean running = true;

    @PostConstruct
    private void init() {
        startWorkerThread();
    }

    @PreDestroy
    private void destroy() {
        running = false;
        cancelAllTasks();
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
        workerThread.interrupt();
        startWorkerThread();
    }

    private void runWorker() {
        while (running) {
            try {
                ScheduledTask task = delayQueue.take();
                task.runnable.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Throwable ignored) {
            }
        }
    }

    private void startWorkerThread() {
        workerThread = new Thread(this::runWorker);
        workerThread.setDaemon(true);
        workerThread.start();
    }

}
