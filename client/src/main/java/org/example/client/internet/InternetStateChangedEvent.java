package org.example.client.internet;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class InternetStateChangedEvent extends ApplicationEvent {

    private final InternetState currentState;

    public InternetStateChangedEvent(Object source, InternetState currentState) {
        super(source);
        this.currentState = currentState;
    }

}
