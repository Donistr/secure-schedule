package org.example.client;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record Config(
        String serverScheme,
        String serverHost,
        Integer serverPort,
        String serverWebsocketClientNameQueryParamName,
        String serverWebsocketDestination,
        String serverSendMessageDestination,
        String serverSubscriptionDestination,
        String clientName
) {

    public URI serverWebsocketConnectUri() {
        return UriComponentsBuilder.newInstance()
                .scheme(serverScheme)
                .host(serverHost)
                .port(serverPort)
                .path(serverWebsocketDestination)
                .queryParam("name", clientName)
                .build()
                .toUri();
    }

}
