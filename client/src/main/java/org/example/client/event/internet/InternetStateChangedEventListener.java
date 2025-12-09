package org.example.client.event.internet;

import lombok.RequiredArgsConstructor;
import org.example.client.service.ChatService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InternetStateChangedEventListener {

    private final ChatService chatService;

    @EventListener
    @Async
    public void onInternetStateChanged(InternetStateChangedEvent event) {
        chatService.handleInternetStateChange(event);
    }

}
