package org.example.server.service;

import org.example.shared.dto.ScheduleDto;

public interface ScheduleService {

    boolean isIntersectsNow(String name1, String name2);

    ScheduleDto get(String name);

    void set(String name, ScheduleDto schedule);

}
