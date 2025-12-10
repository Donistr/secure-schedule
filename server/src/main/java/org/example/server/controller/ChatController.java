package org.example.server.controller;

import lombok.RequiredArgsConstructor;
import org.example.server.config.Config;
import org.example.server.exception.UserNotOnlineException;
import org.example.server.service.UserService;
import org.example.shared.dto.MessageDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final Config config;

    private final SimpMessagingTemplate messagingTemplate;

    private final UserService userService;

    @MessageMapping("/chat")
    public void sendMessage(MessageDto message) {
        String from = message.from();
        String to = message.to();

        if (!userService.isUserOnline(to)) {
            throw new UserNotOnlineException("Пользователь, которому вы пытаетесь отправить сообщение не в сети");
        }

        MessageDto outgoing = new MessageDto(
                from,
                to,
                message.content()
        );

        messagingTemplate.convertAndSendToUser(
                to,
                config.websocketDestination(),
                outgoing
        );

        messagingTemplate.convertAndSendToUser(
                from,
                config.websocketDestination(),
                outgoing
        );
    }

}
