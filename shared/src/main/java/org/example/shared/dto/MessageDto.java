package org.example.shared.dto;

import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MessageDto(
        @NotNull
        String from,
        @NotNull
        String to,
        @NotNull
        String content
) {
}
