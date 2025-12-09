package org.example.server.service;

import org.example.shared.dto.ScheduleDto;

import java.util.Optional;

public interface ScheduleService {

    boolean isIntersectsNow(String name1, String name2);

    Optional<ScheduleDto> get(String name);

    void set(String name, ScheduleDto schedule);

}
