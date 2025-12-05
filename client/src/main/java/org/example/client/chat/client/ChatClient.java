package org.example.client.chat.client;

import org.example.shared.dto.MessageDto;

public interface ChatClient {
    void disconnect();

    void sendMessage(MessageDto message);
}
