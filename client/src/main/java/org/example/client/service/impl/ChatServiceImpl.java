package org.example.client.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.example.client.Config;
import org.example.client.chat.client.ChatClient;
import org.example.client.chat.client.ChatClientFactory;
import org.example.client.exception.ChatConnectionFailedException;
import org.example.client.internet.InternetState;
import org.example.client.internet.InternetStateChangedEvent;
import org.example.client.service.ChatService;
import org.example.client.service.InternetService;
import org.example.shared.dto.MessageDto;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final Config config;

    private final InternetService internetService;

    private final ChatClientFactory chatClientFactory;

    private volatile Optional<ChatClient> client = Optional.empty();

    @Override
    @Synchronized
    public void sendMessage(String receiverName, String message) {
        if (internetService.getState() == InternetState.ENABLED && client.map(c -> !c.isConnected()).orElse(true)) {
            client = createClient();
        }

        client.orElseThrow(() -> new ChatConnectionFailedException("Connection failed"))
                .sendMessage(new MessageDto(
                        config.clientName(),
                        receiverName,
                        message
                ));
    }

    @EventListener
    @Async
    @Synchronized
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return Optional.empty();
    }

}
