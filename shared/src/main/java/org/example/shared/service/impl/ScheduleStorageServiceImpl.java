package org.example.shared.service.impl;

import jakarta.annotation.PostConstruct;
import org.example.shared.dto.ScheduleDto;
import org.example.shared.dto.TimePeriodDto;
import org.example.shared.service.ScheduleStorageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleStorageServiceImpl implements ScheduleStorageService {

    private final Map<String, ScheduleDto> nameToSchedule = new HashMap<>();

    @PostConstruct
    private void init() {
        LocalDateTime startTime = LocalDateTime.of(2025, 12, 10, 14, 42, 0);
        nameToSchedule.put("1", new ScheduleDto(
                startTime,
                startTime.plusMinutes(10),
                new ArrayList<>(List.of(
                        new TimePeriodDto(startTime.plusMinutes(1), startTime.plusMinutes(2)),
                        new TimePeriodDto(startTime.plusMinutes(3), startTime.plusMinutes(4)),
                        new TimePeriodDto(startTime.plusMinutes(5), startTime.plusMinutes(6)),
                        new TimePeriodDto(startTime.plusMinutes(7), startTime.plusMinutes(8))
                )
                )));
        nameToSchedule.put("2", new ScheduleDto(
                startTime,
                startTime.plusMinutes(8),
                new ArrayList<>(List.of(
                        new TimePeriodDto(startTime.plusMinutes(1), startTime.plusMinutes(2)),
                        new TimePeriodDto(startTime.plusMinutes(4), startTime.plusMinutes(5)),
                        new TimePeriodDto(startTime.plusMinutes(5), startTime.plusMinutes(5).plusSeconds(40))
                )
                )));

        nameToSchedule.forEach((name, schedule) -> {
            System.out.println(name + ": " + schedule.from() + " --- " + schedule.to());
            schedule.internetActivePeriods().forEach(period -> System.out.println("\t" + period.from() + " --- " + period.to()));
        });
    }

    @Override
    public Map<String, ScheduleDto> getSchedules() {
        return nameToSchedule;
    }

}
