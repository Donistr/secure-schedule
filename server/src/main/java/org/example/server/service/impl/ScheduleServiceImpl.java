package org.example.server.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.server.service.ScheduleService;
import org.example.shared.dto.ScheduleDto;
import org.example.shared.service.ScheduleStorageService;
import org.springframework.stereotype.Service;

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
    public Optional<ScheduleDto> get(String name) {
        return Optional.ofNullable(nameToSchedule.get(name));
    }

    @Override
    public void set(String name, ScheduleDto schedule) {
        nameToSchedule.put(name, schedule);
    }

}
