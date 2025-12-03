package org.example.server.service;

import jakarta.annotation.PostConstruct;
import org.example.server.exception.ScheduleNotFoundException;
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
        //LocalDateTime time = LocalDateTime.of(2025, 12, 3, 12, 42, 0);
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

    public boolean isIntersectsNow(String name1, String name2) {
        ScheduleDto schedule1 = nameToSchedule.get(name1);
        if (schedule1 == null) {
            throw new ScheduleNotFoundException(String.format("расписание для %s не найдено", name1));
        }

        ScheduleDto schedule2 = nameToSchedule.get(name2);
        if (schedule2 == null) {
            throw new ScheduleNotFoundException(String.format("расписание для %s не найдено", name2));
        }

        LocalDateTime now = LocalDateTime.now();
        if (!schedule1.from().isBefore(now)) {
            System.out.println(111);
            return false;
        }
        if (!schedule2.from().isBefore(now)) {
            System.out.println(222);
            return false;
        }

        if (!schedule1.to().isAfter(now)) {
            System.out.println(333);
            return false;
        }
        if (!schedule2.to().isAfter(now)) {
            System.out.println(444);
            return false;
        }

        return true;
    }

    public ScheduleDto get(String name) {
        return nameToSchedule.get(name);
    }

    public void set(String name, ScheduleDto schedule) {
        nameToSchedule.put(name, schedule);
    }

}
