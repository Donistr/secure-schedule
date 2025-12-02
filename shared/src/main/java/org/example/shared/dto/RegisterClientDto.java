package org.example.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record RegisterClientDto(
        @JsonProperty("name")
        @NotNull String name
) {
}
