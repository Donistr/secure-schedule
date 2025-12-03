package org.example.client.chat.client;

import org.springframework.stereotype.Component;

@Component
public class ChatClientFactory {

    public ChatClient create(String clientName) {
        return new ChatClient(new SessionHandler(clientName), clientName);
    }

}
