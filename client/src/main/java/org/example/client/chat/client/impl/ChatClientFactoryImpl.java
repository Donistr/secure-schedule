package org.example.client.chat.client.impl;

import org.example.client.chat.client.ChatClient;
import org.example.client.chat.client.ChatClientFactory;
import org.springframework.stereotype.Component;

@Component
public class ChatClientFactoryImpl implements ChatClientFactory {

    @Override
    public ChatClient create(String clientName) {
        return new ChatClientImpl(new SessionHandler(clientName), clientName);
    }

}
