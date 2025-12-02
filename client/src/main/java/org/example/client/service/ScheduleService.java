package org.example.client.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.client.tmp.Schedule;
import org.example.client.tmp.TimePeriod;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private static final long WINDOW_TO_GET_NEW_SCHEDULE_SECONDS = 5;

    private static final long BETWEEN_WINDOWS_TO_GET_NEW_SCHEDULE_SECONDS = 60;

    private final InternetService internetService;

    private final QueueTaskScheduler queueTaskScheduler;

    private volatile LocalDateTime currentScheduleEnd = LocalDateTime.now();

    @PostConstruct
    private void postConstruct() {
        /*LocalDateTime now = LocalDateTime.now().plusSeconds(3);
        for (int i = 0; i < 1000; ++i) {
            if (i == 500) {
                queueTaskScheduler.scheduleTaskAt(now, queueTaskScheduler::cancelAllTasks);
            }

            int finalI = i;
            queueTaskScheduler.scheduleTaskAt(now, () -> System.out.println(finalI));
        }*/

        triggerWindowCheckAt(LocalDateTime.now());

        /*Schedule schedule = new Schedule(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(3).plusSeconds(15),
                List.of(
                        new TimePeriod(LocalDateTime.now().plusSeconds(7), LocalDateTime.now().plusMinutes(1).plusSeconds(3)),
                        new TimePeriod(LocalDateTime.now().plusMinutes(2).plusSeconds(13), LocalDateTime.now().plusMinutes(2).plusSeconds(23))
                )
        );
        setSchedule(schedule);*/
    }

    public synchronized void setSchedule(Schedule schedule) {
        queueTaskScheduler.cancelAllTasks();
        currentScheduleEnd = schedule.end();
        internetService.disableInternet();
        scheduleActivePeriods(schedule);
        triggerWindowCheckAt(currentScheduleEnd);
    }

    private void scheduleActivePeriods(Schedule schedule) {
        for (TimePeriod period : schedule.internetActivePeriods()) {
            queueTaskScheduler.scheduleTaskAt(period.start(), internetService::enableInternet);
            queueTaskScheduler.scheduleTaskAt(period.end(), internetService::disableInternet);
        }
    }

    private void triggerWindowCheckAt(LocalDateTime dateTime) {
        queueTaskScheduler.scheduleTaskAt(dateTime, this::openWindowIfScheduleExpired);
    }

    private void openWindowIfScheduleExpired() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(currentScheduleEnd)) {
            return;
        }

        queueTaskScheduler.scheduleTaskAt(now, internetService::enableInternet);
        queueTaskScheduler.scheduleTaskAt(now.plusSeconds(WINDOW_TO_GET_NEW_SCHEDULE_SECONDS), internetService::disableInternet);
        triggerWindowCheckAt(now.plusSeconds(BETWEEN_WINDOWS_TO_GET_NEW_SCHEDULE_SECONDS));
    }

}
