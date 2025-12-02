package org.example.client.service;

import java.time.LocalDateTime;

public interface QueueTaskSchedulerService {

    void scheduleTaskAt(LocalDateTime dateTime, Runnable task);

    void cancelAllTasks();

}
