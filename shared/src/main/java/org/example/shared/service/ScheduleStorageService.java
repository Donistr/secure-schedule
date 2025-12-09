package org.example.shared.service;

import org.example.shared.dto.ScheduleDto;

import java.util.Map;

public interface ScheduleStorageService {
    Map<String, ScheduleDto> getSchedules();
}
