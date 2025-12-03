package org.example.client.chat.client;

import lombok.RequiredArgsConstructor;
import org.example.shared.dto.MessageDto;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;

@RequiredArgsConstructor
public class SessionHandler extends StompSessionHandlerAdapter {

    private static final String SUBSCRIPTION_DESTINATION = "/user/queue/messages";

    private final String clientName;

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe(SUBSCRIPTION_DESTINATION, this);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return MessageDto.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        MessageDto msg = (MessageDto) payload;

        String from = msg.from();
        if (from.equals(clientName)) {
            System.out.printf("Вы -> %s: %s\n", msg.to(), msg.content());
        } else {
            System.out.printf("%s: %s\n", from, msg.content());
        }
    }

    /*@Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
                                Throwable exception) {
        throw new ChatSessionException(exception);
    }*/

}
