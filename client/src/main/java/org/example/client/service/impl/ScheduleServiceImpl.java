package org.example.client.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.client.service.InternetService;
import org.example.client.service.QueueTaskSchedulerService;
import org.example.client.service.ScheduleService;
import org.example.shared.dto.ScheduleDto;
import org.example.shared.dto.TimePeriodDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final InternetService internetService;

    private final QueueTaskSchedulerService queueTaskSchedulerService;

    @PostConstruct
    private void postConstruct() {
        internetService.disableInternet();

        LocalDateTime time = LocalDateTime.now();
        //LocalDateTime time = LocalDateTime.of(2025, 12, 3, 12, 42, 0);
        setSchedule(new ScheduleDto(
                time,
                time.plusMinutes(5),
                new ArrayList<>(List.of(
                        new TimePeriodDto(time, time.plusMinutes(1)),
                        new TimePeriodDto(time.plusMinutes(1).plusSeconds(50), time.plusMinutes(2).plusSeconds(10)),
                        new TimePeriodDto(time.plusMinutes(3), time.plusMinutes(3).plusSeconds(45)),
                        new TimePeriodDto(time.plusMinutes(4), time.plusMinutes(4).plusSeconds(20))
                ))
        ));
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
