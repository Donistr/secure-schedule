package org.example.client.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.client.service.InternetService;
import org.example.client.service.QueueTaskSchedulerService;
import org.example.client.service.ScheduleService;
import org.example.client.dto.ScheduleDto;
import org.example.client.dto.TimePeriodDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private static final long WINDOW_TO_GET_NEW_SCHEDULE_SECONDS = 5;

    private static final long BETWEEN_WINDOWS_TO_GET_NEW_SCHEDULE_SECONDS = 60;

    private final InternetService internetService;

    private final QueueTaskSchedulerService queueTaskSchedulerService;

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

    @Override
    public synchronized void setSchedule(ScheduleDto scheduleDto) {
        queueTaskSchedulerService.cancelAllTasks();
        currentScheduleEnd = scheduleDto.to();
        internetService.disableInternet();
        scheduleActivePeriods(scheduleDto);
        triggerWindowCheckAt(currentScheduleEnd);
    }

    private void scheduleActivePeriods(ScheduleDto scheduleDto) {
        for (TimePeriodDto period : scheduleDto.internetActivePeriods()) {
            queueTaskSchedulerService.scheduleTaskAt(period.from(), internetService::enableInternet);
            queueTaskSchedulerService.scheduleTaskAt(period.to(), internetService::disableInternet);
        }
    }

    private void triggerWindowCheckAt(LocalDateTime dateTime) {
        queueTaskSchedulerService.scheduleTaskAt(dateTime, this::openWindowIfScheduleExpired);
    }

    private void openWindowIfScheduleExpired() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(currentScheduleEnd)) {
            return;
        }

        queueTaskSchedulerService.scheduleTaskAt(now, internetService::enableInternet);
        queueTaskSchedulerService.scheduleTaskAt(now.plusSeconds(WINDOW_TO_GET_NEW_SCHEDULE_SECONDS), internetService::disableInternet);
        triggerWindowCheckAt(now.plusSeconds(BETWEEN_WINDOWS_TO_GET_NEW_SCHEDULE_SECONDS));
    }

}
