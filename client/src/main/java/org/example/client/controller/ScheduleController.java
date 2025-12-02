package org.example.client.controller;

import lombok.RequiredArgsConstructor;
import org.example.client.service.ScheduleService;
import org.example.shared.dto.ScheduleDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createSchedule(@RequestBody ScheduleDto scheduleDto) {
        scheduleService.setSchedule(scheduleDto);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
