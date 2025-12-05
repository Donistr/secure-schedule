package org.example.client.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.client.chat.client.ChatClient;
import org.example.client.chat.client.ChatClientFactory;
import org.example.client.exception.ChatConnectionFailedException;
import org.example.client.internet.InternetStateChangedEvent;
import org.example.client.service.ChatService;
import org.example.shared.dto.MessageDto;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final static String CLIENT_NAME = "1";

    private final ChatClientFactory chatClientFactory;

    private Optional<ChatClient> client = Optional.empty();

    @Override
    public void sendMessage(String receiverName, String message) {
        client.orElseThrow(() -> new ChatConnectionFailedException("Connection failed"))
                .sendMessage(new MessageDto(
                        CLIENT_NAME,
                        receiverName,
                        message
                ));
    }

    @EventListener
    @Async
    public void handleInternetStateChange(InternetStateChangedEvent event) {
        switch (event.getCurrentState()) {
            case ENABLED -> client = createClient();
            case DISABLED -> {
                client.ifPresent(ChatClient::disconnect);
                client = Optional.empty();
            }
        }
    }

    private Optional<ChatClient> createClient() {
        try {
            return Optional.of(chatClientFactory.create(CLIENT_NAME));
        } catch (Exception ignored) {
        }

        return Optional.empty();
    }

}
