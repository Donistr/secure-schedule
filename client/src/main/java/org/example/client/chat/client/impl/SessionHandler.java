package org.example.client.chat.client.impl;

import lombok.RequiredArgsConstructor;
import org.example.client.Config;
import org.example.shared.dto.MessageDto;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;

@RequiredArgsConstructor
public class SessionHandler extends StompSessionHandlerAdapter {

    private final Config config;

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe(config.serverSubscriptionDestination(), this);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return MessageDto.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        MessageDto msg = (MessageDto) payload;

        String from = msg.from();
        if (from.equals(config.clientName())) {
            System.out.printf("Вы -> %s: %s\n", msg.to(), msg.content());
        } else {
            System.out.printf("%s: %s\n", from, msg.content());
        }
    }

}
