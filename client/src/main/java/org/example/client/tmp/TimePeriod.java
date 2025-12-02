package org.example.client.tmp;

import java.time.LocalDateTime;

public record TimePeriod(
        LocalDateTime start,
        LocalDateTime end
) {
}
