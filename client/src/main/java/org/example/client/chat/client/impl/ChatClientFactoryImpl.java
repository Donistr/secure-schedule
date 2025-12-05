package org.example.client.chat.client.impl;

import lombok.RequiredArgsConstructor;
import org.example.client.Config;
import org.example.client.chat.client.ChatClient;
import org.example.client.chat.client.ChatClientFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatClientFactoryImpl implements ChatClientFactory {

    private final Config config;

    @Override
    public ChatClient create() {
        return new ChatClientImpl(
                config.serverWebsocketConnectUri(),
                config.serverSendMessageDestination(),
                new SessionHandler(config.serverSubscriptionDestination(), config.clientName())
        );
    }

}
