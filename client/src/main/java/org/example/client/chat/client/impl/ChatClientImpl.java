package org.example.client.chat.client.impl;

import org.example.client.Config;
import org.example.client.chat.client.ChatClient;
import org.example.client.exception.ChatConnectionFailedException;
import org.example.shared.dto.MessageDto;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ChatClientImpl implements ChatClient {

    private final Config config;

    private final StompSession session;

    public ChatClientImpl(Config config, StompSessionHandlerAdapter sessionHandler) {
        try {
            this.config = config;

            List<Transport> transports = List.of(new WebSocketTransport(new StandardWebSocketClient()));
            SockJsClient sockJsClient = new SockJsClient(transports);

            WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
            stompClient.setMessageConverter(new JacksonJsonMessageConverter());

            CompletableFuture<StompSession> sessionFuture = stompClient.connectAsync(
                    config.serverWebsocketConnectUri(),
                    new WebSocketHttpHeaders(),
                    new StompHeaders(),
                    sessionHandler
            );

            session = sessionFuture.get();
        } catch (Exception e) {
            throw new ChatConnectionFailedException(e);
        }

        System.out.println("Connected");
    }

    @Override
    public void sendMessage(MessageDto message) {
        try {
            session.send(config.serverSendMessageDestination(), message);
        } catch (Exception e) {
            throw new ChatConnectionFailedException(e);
        }
    }

    @Override
    public boolean isConnected() {
        return session.isConnected();
    }

}
