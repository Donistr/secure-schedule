package org.example.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.server.config.Config;
import org.example.server.service.ScheduleService;
import org.example.server.service.UserService;
import org.example.shared.dto.ScheduleDto;
import org.example.shared.dto.TimePeriodDto;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Config config;

    private final SimpUserRegistry userRegistry;

    private final ScheduleService scheduleService;

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public boolean isUserOnline(String username) {
        return userRegistry.getUser(username) != null;
    }

    @Override
    public Optional<TimePeriodDto> getCurrentActivePeriodForUser(String name) {
        Optional<ScheduleDto> scheduleOptional = scheduleService.get(name);
        if (scheduleOptional.isEmpty()) {
            return Optional.empty();
        }
        ScheduleDto schedule = scheduleOptional.get();

        if (!schedule.to().isAfter(LocalDateTime.now())) {
            return Optional.empty();
        }

        for (TimePeriodDto period : schedule.internetActivePeriods()) {
            if (period.from().isAfter(LocalDateTime.now())) {
                return Optional.empty();
            }

            if (!period.to().isBefore(LocalDateTime.now())) {
                return Optional.of(period);
            }
        }

        return Optional.empty();
    }

    @Override
    public void disconnectUser(String name) {
        SimpUser user = userRegistry.getUser(name);
        if (user == null) {
            return;
        }

        for (SimpSession session : user.getSessions()) {
            try {
                SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.DISCONNECT);
                headerAccessor.setSessionId(session.getId());
                headerAccessor.setUser(() -> name);

                messagingTemplate.send(
                        config.websocketDestination(),
                        MessageBuilder.createMessage(new byte[0], headerAccessor.getMessageHeaders())
                );
            } catch (Exception e) {
                System.err.println("Failed to disconnect session: " + e.getMessage());
            }
        }
    }

}
