package org.example.server.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.server.exception.ScheduleNotFoundException;
import org.example.server.service.ScheduleService;
import org.example.shared.dto.ScheduleDto;
import org.example.shared.service.ScheduleStorageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleStorageService scheduleStorageService;

    private Map<String, ScheduleDto> nameToSchedule;

    @PostConstruct
    private void postConstruct() {
        nameToSchedule = scheduleStorageService.getSchedules();
    }

    @Override
    public boolean isIntersectsNow(String name1, String name2) {
        ScheduleDto schedule1 = nameToSchedule.get(name1);
        if (schedule1 == null) {
            throw new ScheduleNotFoundException(String.format("Расписание для %s не найдено", name1));
        }

        ScheduleDto schedule2 = nameToSchedule.get(name2);
        if (schedule2 == null) {
            throw new ScheduleNotFoundException(String.format("Расписание для %s не найдено", name2));
        }

        LocalDateTime now = LocalDateTime.now();
        if (!schedule1.from().isBefore(now)) {
            return false;
        }
        if (!schedule2.from().isBefore(now)) {
            return false;
        }

        if (!schedule1.to().isAfter(now)) {
            return false;
        }
        if (!schedule2.to().isAfter(now)) {
            return false;
        }

        return true;
    }

    @Override
    public Optional<ScheduleDto> get(String name) {
        return Optional.ofNullable(nameToSchedule.get(name));
    }

    @Override
    public void set(String name, ScheduleDto schedule) {
        nameToSchedule.put(name, schedule);
    }

}
