package org.example.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TimePeriodDto(
        @JsonProperty("from")
        @NotNull LocalDateTime from,
        @JsonProperty("to")
        @NotNull LocalDateTime to
) {
}
