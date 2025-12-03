package org.example.server.controller;

import lombok.RequiredArgsConstructor;
import org.example.server.exception.UnavailableNowException;
import org.example.server.service.ScheduleService;
import org.example.shared.dto.MessageDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    private final ScheduleService scheduleService;

    @MessageMapping("/chat")
    public void sendPrivate(MessageDto message) {
        try {
            if (!scheduleService.isIntersectsNow(message.from(), message.to())) {
                throw new UnavailableNowException("Расписания клиентов не пересекаются в текущий момент времени");
            }
        } catch (Exception e) {
            MessageDto outgoing = new MessageDto(
                    "server",
                    message.from(),
                    String.format("ошибка: %s", e.getMessage())
            );

            messagingTemplate.convertAndSendToUser(
                    message.from(),
                    "/queue/messages",
                    outgoing
            );

            return;
        }

        MessageDto outgoing = new MessageDto(
                message.from(),
                message.to(),
                message.content()
        );

        messagingTemplate.convertAndSendToUser(
                message.to(),
                "/queue/messages",
                outgoing
        );

        messagingTemplate.convertAndSendToUser(
                message.from(),
                "/queue/messages",
                outgoing
        );
    }

}
