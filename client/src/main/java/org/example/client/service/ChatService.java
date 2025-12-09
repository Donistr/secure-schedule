package org.example.client.service;

import org.example.client.internet.InternetStateChangedEvent;

public interface ChatService {

    void sendMessage(String receiverName, String message);

    void handleInternetStateChange(InternetStateChangedEvent event);

}
