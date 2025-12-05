package org.example.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record Config(
        String websocketDestinationPrefix
) {

    private static final String DEFAULT_DESTINATION_ENDING = "/messages";

    public String websocketDestination() {
        return websocketDestinationPrefix + DEFAULT_DESTINATION_ENDING;
    }

}
