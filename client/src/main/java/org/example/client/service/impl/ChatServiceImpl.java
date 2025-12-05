package org.example.client.service.impl;

import org.example.client.Config;
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
public class ChatServiceImpl implements ChatService {

    private final String clientName;

    private final ChatClientFactory chatClientFactory;

    private Optional<ChatClient> client = Optional.empty();

    public ChatServiceImpl(Config config, ChatClientFactory chatClientFactory) {
        this.clientName = config.clientName();
        this.chatClientFactory = chatClientFactory;
    }

    @Override
    public void sendMessage(String receiverName, String message) {
        client.orElseThrow(() -> new ChatConnectionFailedException("Connection failed"))
                .sendMessage(new MessageDto(
                        clientName,
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
            return Optional.of(chatClientFactory.create());
        } catch (Exception ignored) {
        }

        return Optional.empty();
    }

}
