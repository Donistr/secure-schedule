package org.example.client.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.client.service.InternetService;
import org.example.client.service.QueueTaskSchedulerService;
import org.example.client.service.ScheduleService;
import org.example.shared.dto.ScheduleDto;
import org.example.shared.dto.TimePeriodDto;
import org.springframework.http.client.ReactorClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private static final String CLIENT_NAME = "1";

    private static final long REQUEST_TIMEOUT_SECONDS = 5;

    private static final long BETWEEN_TRIES_TO_GET_NEW_SCHEDULE_SECONDS = 60;

    private final InternetService internetService;

    private final QueueTaskSchedulerService queueTaskSchedulerService;

    private final RestClient client = createClient();

    private RestClient createClient() {
        ReactorClientHttpRequestFactory factory = new ReactorClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS));
        factory.setReadTimeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS));

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl("http://127.0.0.1:8080")
                .build();
    }

    @PostConstruct
    private void postConstruct() {
        scheduleGettingSchedule(LocalDateTime.now());
    }

    @Override
    public synchronized void setSchedule(ScheduleDto scheduleDto) {
        scheduleDto.internetActivePeriods().removeIf(period -> !period.to().isAfter(LocalDateTime.now()));

        queueTaskSchedulerService.cancelAllTasks();
        internetService.disableInternet();
        scheduleActivePeriods(scheduleDto);
        scheduleGettingSchedule(scheduleDto.to());
    }

    private void scheduleActivePeriods(ScheduleDto scheduleDto) {
        for (TimePeriodDto period : scheduleDto.internetActivePeriods()) {
            queueTaskSchedulerService.scheduleTaskAt(period.from(), internetService::enableInternet);
            queueTaskSchedulerService.scheduleTaskAt(period.to(), internetService::disableInternet);
        }
    }

    private void scheduleGettingSchedule(LocalDateTime dateTime) {
        queueTaskSchedulerService.scheduleTaskAt(dateTime, this::scheduleTryGetScheduleFromServer);
    }

    private void scheduleTryGetScheduleFromServer() {
        Optional<ScheduleDto> schedule = Optional.empty();
        internetService.enableInternet();
        for (int i = 0; i < 3 && schedule.isEmpty(); ++i) {
            schedule = tryGetScheduleFromServer();
        }
        internetService.disableInternet();

        if (schedule.isEmpty()) {
            queueTaskSchedulerService.scheduleTaskAt(
                    LocalDateTime.now().plusSeconds(BETWEEN_TRIES_TO_GET_NEW_SCHEDULE_SECONDS),
                    this::scheduleTryGetScheduleFromServer
            );

            return;
        }

        setSchedule(schedule.get());
    }

    private Optional<ScheduleDto> tryGetScheduleFromServer() {
        try {
            ScheduleDto result = client.get()
                    .uri(uriBuilder ->
                            uriBuilder.path("/api/schedule")
                                    .queryParam("name", CLIENT_NAME)
                                    .build()
                    )
                    .retrieve()
                    .body(ScheduleDto.class);

            return Optional.ofNullable(result);
        } catch (Exception ignored) {
        }

        return Optional.empty();
    }

}
