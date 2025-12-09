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
        LocalDateTime startTime = LocalDateTime.of(2025, 12, 9, 19, 35, 0);
        nameToSchedule.put("1", new ScheduleDto(
                startTime,
                startTime.plusMinutes(5),
                new ArrayList<>(List.of(
                        new TimePeriodDto(startTime.plusSeconds(30), startTime.plusMinutes(1)),
                        new TimePeriodDto(startTime.plusMinutes(1).plusSeconds(50), startTime.plusMinutes(2).plusSeconds(10)),
                        new TimePeriodDto(startTime.plusMinutes(3), startTime.plusMinutes(3).plusSeconds(45)),
                        new TimePeriodDto(startTime.plusMinutes(4), startTime.plusMinutes(4).plusSeconds(20))
                )
        )));
        nameToSchedule.put("2", new ScheduleDto(
                startTime,
                startTime.plusMinutes(3),
                new ArrayList<>(List.of(
                        new TimePeriodDto(startTime.plusSeconds(30), startTime.plusMinutes(1)),
                        new TimePeriodDto(startTime.plusMinutes(1).plusSeconds(15), startTime.plusMinutes(1).plusSeconds(30)),
                        new TimePeriodDto(startTime.plusMinutes(2), startTime.plusMinutes(2).plusSeconds(10))
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
