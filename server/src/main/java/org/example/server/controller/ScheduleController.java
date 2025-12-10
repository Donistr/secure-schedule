package org.example.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.server.service.ScheduleService;
import org.example.shared.dto.ScheduleDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<ScheduleDto> getSchedule(@RequestParam("name") String name) {
        return ResponseEntity.ok(scheduleService.get(name).orElse(null));
    }

    @PostMapping
    public ResponseEntity<Void> createSchedule(@RequestParam("name") String name, @Valid @RequestBody ScheduleDto scheduleDto) {
        scheduleService.set(name, scheduleDto);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
