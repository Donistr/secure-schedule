package org.example.server.service;

import jakarta.annotation.PostConstruct;
import org.example.shared.dto.ScheduleDto;
import org.example.shared.dto.TimePeriodDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleService {

    private final Map<String, ScheduleDto> nameToSchedule = new HashMap<>();

    @PostConstruct
    private void postConstruct() {
        LocalDateTime time = LocalDateTime.now();
        nameToSchedule.put("1", new ScheduleDto(
                time,
                time.plusMinutes(5),
                List.of(
                        new TimePeriodDto(time.plusSeconds(30), time.plusMinutes(1)),
                        new TimePeriodDto(time.plusMinutes(1).plusSeconds(50), time.plusMinutes(2).plusSeconds(10)),
                        new TimePeriodDto(time.plusMinutes(3), time.plusMinutes(3).plusSeconds(45)),
                        new TimePeriodDto(time.plusMinutes(4), time.plusMinutes(4).plusSeconds(20))
                )
        ));
        nameToSchedule.put("2", new ScheduleDto(
                time,
                time.plusMinutes(3),
                List.of(
                        new TimePeriodDto(time.plusSeconds(30), time.plusMinutes(1)),
                        new TimePeriodDto(time.plusMinutes(1).plusSeconds(15), time.plusMinutes(1).plusSeconds(30)),
                        new TimePeriodDto(time.plusMinutes(2), time.plusMinutes(2).plusSeconds(10))
                )
        ));
    }

    public ScheduleDto get(String name) {
        return nameToSchedule.get(name);
    }

    public void set(String name, ScheduleDto schedule) {
        nameToSchedule.put(name, schedule);
    }

}
