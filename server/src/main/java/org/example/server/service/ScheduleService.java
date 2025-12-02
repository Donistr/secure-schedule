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
        LocalDateTime now = LocalDateTime.now();
        nameToSchedule.put("1", new ScheduleDto(
                now,
                now.plusMinutes(5),
                List.of(
                        new TimePeriodDto(now.plusSeconds(30), now.plusMinutes(1)),
                        new TimePeriodDto(now.plusMinutes(1).plusSeconds(50), now.plusMinutes(2).plusSeconds(10)),
                        new TimePeriodDto(now.plusMinutes(3), now.plusMinutes(3).plusSeconds(45)),
                        new TimePeriodDto(now.plusMinutes(4), now.plusMinutes(4).plusSeconds(20))
                )
        ));
    }

    public ScheduleDto get(String name) {
        ScheduleDto schedule = nameToSchedule.get(name);
        if (schedule == null) {
            return createDefaultSchedule();
        }

        return schedule;
    }

    public void set(String name, ScheduleDto schedule) {
        nameToSchedule.put(name, schedule);
    }

    private ScheduleDto createDefaultSchedule() {
        LocalDateTime now = LocalDateTime.now();
        return new ScheduleDto(
                now,
                now.plusMinutes(3),
                List.of(
                        new TimePeriodDto(now.plusSeconds(30), now.plusMinutes(1)),
                        new TimePeriodDto(now.plusMinutes(1).plusSeconds(15), now.plusMinutes(1).plusSeconds(30)),
                        new TimePeriodDto(now.plusMinutes(2), now.plusMinutes(2).plusSeconds(10))
                )
        );
    }

}
