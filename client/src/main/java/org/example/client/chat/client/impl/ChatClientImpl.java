package org.example.client.chat.client.impl;

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

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ChatClientImpl implements ChatClient {

    private final String serverSendMessageDestination;

    private final StompSession session;

    public ChatClientImpl(URI serverWebsocketConnectUri,
                          String serverSendMessageDestination,
                          StompSessionHandlerAdapter sessionHandler
    ) {
        try {
            this.serverSendMessageDestination = serverSendMessageDestination;

            List<Transport> transports = List.of(new WebSocketTransport(new StandardWebSocketClient()));
            SockJsClient sockJsClient = new SockJsClient(transports);

            WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
            stompClient.setMessageConverter(new JacksonJsonMessageConverter());

            CompletableFuture<StompSession> sessionFuture = stompClient.connectAsync(
                    serverWebsocketConnectUri,
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
    public void disconnect() {
        session.disconnect();
    }

    @Override
    public void sendMessage(MessageDto message) {
        try {
            session.send(serverSendMessageDestination, message);
        } catch (Exception e) {
            throw new ChatConnectionFailedException(e);
        }
    }

}
