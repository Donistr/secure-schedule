package org.example.server.controller;

import lombok.RequiredArgsConstructor;
import org.example.shared.dto.MessageDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void sendPrivate(MessageDto message) {
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
