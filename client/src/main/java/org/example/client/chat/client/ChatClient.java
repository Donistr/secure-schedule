package org.example.client.chat.client;

import org.example.shared.dto.MessageDto;

public interface ChatClient {

    void sendMessage(MessageDto message);

    boolean isConnected();

}
