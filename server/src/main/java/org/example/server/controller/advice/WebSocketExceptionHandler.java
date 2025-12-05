package org.example.server.controller.advice;

import lombok.RequiredArgsConstructor;
import org.example.shared.dto.MessageDto;
import org.example.shared.exception.BaseException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.security.Principal;

@ControllerAdvice
@RequiredArgsConstructor
public class WebSocketExceptionHandler {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageExceptionHandler(RuntimeException.class)
    public void handleUnavailableNowException(RuntimeException ex, SimpMessageHeaderAccessor headerAccessor) {
        sendErrorToUser(headerAccessor, ex.getMessage());
    }

    @MessageExceptionHandler(BaseException.class)
    public void handleUnavailableNowException(BaseException ex, SimpMessageHeaderAccessor headerAccessor) {
        sendErrorToUser(headerAccessor, ex.getMessage());
    }

    private void sendErrorToUser(SimpMessageHeaderAccessor headerAccessor, String errorMessage) {
        Principal user = headerAccessor != null ? headerAccessor.getUser() : null;
        if (user == null) {
            return;
        }
        String userName = user.getName();

        MessageDto errorDto = new MessageDto(
                "server",
                userName,
                "ошибка: " + errorMessage
        );

        messagingTemplate.convertAndSendToUser(
                userName,
                "/queue/messages",
                errorDto
        );
    }

}
