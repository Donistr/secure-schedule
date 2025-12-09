package org.example.client.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.client.Config;
import org.example.client.service.InternetService;
import org.example.client.service.QueueTaskSchedulerService;
import org.example.client.service.ScheduleService;
import org.example.shared.dto.ScheduleDto;
import org.example.shared.dto.TimePeriodDto;
import org.example.shared.service.ScheduleStorageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final Config config;

    private final InternetService internetService;

    private final QueueTaskSchedulerService queueTaskSchedulerService;

    private final ScheduleStorageService scheduleStorageService;

    @PostConstruct
    private void postConstruct() {
        internetService.disableInternet();

        setSchedule(scheduleStorageService.getSchedules().get(config.clientName()));
    }

    @Override
    public synchronized void setSchedule(ScheduleDto scheduleDto) {
        queueTaskSchedulerService.cancelAllTasks();
        internetService.disableInternet();
        scheduleDto.internetActivePeriods().removeIf(period -> !period.to().isAfter(LocalDateTime.now()));
        scheduleActivePeriods(scheduleDto);
    }

    private void scheduleActivePeriods(ScheduleDto scheduleDto) {
        for (TimePeriodDto period : scheduleDto.internetActivePeriods()) {
            queueTaskSchedulerService.scheduleTaskAt(period.from(), internetService::enableInternet);
            queueTaskSchedulerService.scheduleTaskAt(period.to(), internetService::disableInternet);
        }
    }

}
