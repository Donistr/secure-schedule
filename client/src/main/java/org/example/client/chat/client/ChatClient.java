package org.example.client.chat.client;

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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ChatClient {

    private static final String URL = "http://127.0.0.1:8080/ws";

    private static final String SEND_MESSAGE_DESTINATION = "/api/chat";

    private final StompSession session;

    public ChatClient(StompSessionHandlerAdapter sessionHandler, String clientName) {
        try {
            List<Transport> transports = List.of(new WebSocketTransport(new StandardWebSocketClient()));
            SockJsClient sockJsClient = new SockJsClient(transports);

            WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
            stompClient.setMessageConverter(new JacksonJsonMessageConverter());

            CompletableFuture<StompSession> sessionFuture = stompClient.connectAsync(
                    URL + "?name=" + URLEncoder.encode(clientName, StandardCharsets.UTF_8),
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

    public void sendMessage(MessageDto message) {
        try {
            session.send(SEND_MESSAGE_DESTINATION, message);
        } catch (Exception e) {
            throw new ChatConnectionFailedException(e);
        }
    }

}
