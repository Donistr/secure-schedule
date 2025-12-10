package org.example.server.service;

import org.example.shared.dto.ScheduleDto;

import java.util.Optional;

public interface ScheduleService {

    Optional<ScheduleDto> get(String name);

}
