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
        LocalDateTime time = LocalDateTime.of(2025, 12, 9, 15, 23, 0);
        nameToSchedule.put("1", new ScheduleDto(
                time,
                time.plusMinutes(5),
                new ArrayList<>(List.of(
                        new TimePeriodDto(time.plusSeconds(30), time.plusMinutes(1)),
                        new TimePeriodDto(time.plusMinutes(1).plusSeconds(50), time.plusMinutes(2).plusSeconds(10)),
                        new TimePeriodDto(time.plusMinutes(3), time.plusMinutes(3).plusSeconds(45)),
                        new TimePeriodDto(time.plusMinutes(4), time.plusMinutes(4).plusSeconds(20))
                )
        )));
        nameToSchedule.put("2", new ScheduleDto(
                time,
                time.plusMinutes(3),
                new ArrayList<>(List.of(
                        new TimePeriodDto(time.plusSeconds(30), time.plusMinutes(1)),
                        new TimePeriodDto(time.plusMinutes(1).plusSeconds(15), time.plusMinutes(1).plusSeconds(30)),
                        new TimePeriodDto(time.plusMinutes(2), time.plusMinutes(2).plusSeconds(10))
                )
        )));
        System.out.println(nameToSchedule);
    }

    @Override
    public Map<String, ScheduleDto> getSchedules() {
        return nameToSchedule;
    }

}
