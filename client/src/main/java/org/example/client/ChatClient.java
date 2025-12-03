package org.example.client;

import org.example.shared.dto.MessageDto;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class ChatClient {

    private static final String URL = "http://127.0.0.1:8080/ws";

    public static void main(String[] args) throws Exception {
        System.out.print("Введите ваш ник: ");
        String name = new Scanner(System.in).nextLine().trim();

        // Настраиваем клиент с поддержкой SockJS (как в браузере)
        List<Transport> transports = List.of(
                new WebSocketTransport(new StandardWebSocketClient())
        );
        SockJsClient sockJsClient = new SockJsClient(transports);

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new JacksonJsonMessageConverter());

        // Создаём сессию
        StompSessionHandlerAdapter handler = new MySessionHandler();

        StompHeaders connectHeaders = new StompHeaders();
        //connectHeaders.add("name", name);

        WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();

        StompSession session = stompClient.connectAsync(
                URL + "?name=" + URLEncoder.encode(name, StandardCharsets.UTF_8),
                handshakeHeaders,
                connectHeaders,
                handler
        ).get();

        /*StompSession session = stompClient.connectAsync(URL, null,
                new StompHeaders() {{ put("name", List.of(name)); }}, // ← передаём ник!
                handler).get(); // блокируемся, пока не подключимся*/

        System.out.println("Подключён как: " + name);
        System.out.println("Пиши: получатель сообщение");
        System.out.println("Например: Боб Привет, как дела?");
        System.out.println("Для выхода: exit");

        // Читаем ввод из консоли
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            if ("exit".equalsIgnoreCase(line)) break;

            String[] parts = line.split("\\s+", 2);
            if (parts.length < 2) {
                System.out.println("Формат: получатель текст");
                continue;
            }

            String to = parts[0];
            String text = parts[1];

            MessageDto msg = new MessageDto(name, to, text);
            session.send("/api/chat", msg);
        }

        session.disconnect();
        System.exit(0);
    }

    static class MySessionHandler extends StompSessionHandlerAdapter {

        /*private final String myName;

        public MySessionHandler(String myName) {
            this.myName = myName;
        }*/

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            session.subscribe("/user/queue/messages", this);
            System.out.println("Готов к приёму сообщений...");
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return MessageDto.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            MessageDto msg = (MessageDto) payload;
            System.out.printf("\n%s: %s\n> ", msg.from(), msg.content());
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
                                    Throwable exception) {
            exception.printStackTrace();
        }
    }

}
