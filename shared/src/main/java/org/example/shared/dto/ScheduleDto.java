package org.example.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.example.shared.exception.IncorrectScheduleException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record ScheduleDto(
        @JsonProperty("from")
        @NotNull LocalDateTime from,
        @JsonProperty("to")
        @NotNull LocalDateTime to,
        @JsonProperty("internet_active_periods")
        @NotNull List<TimePeriodDto> internetActivePeriods
) {

    public ScheduleDto(LocalDateTime from, LocalDateTime to, List<TimePeriodDto> internetActivePeriods) {
        if (internetActivePeriods == null) {
            internetActivePeriods = new ArrayList<>();
        }

        this.from = from;
        this.to = to;
        this.internetActivePeriods = internetActivePeriods;

        validation();
    }

    private void validation() {
        if (from == null || to == null || from.isAfter(to)) {
            throw new IncorrectScheduleException("from == null || to == null || from.isAfter(to)");
        }

        if (!to.isAfter(LocalDateTime.now())) {
            throw new IncorrectScheduleException("!to.isAfter(LocalDateTime.now())");
        }

        LocalDateTime previousEnd = null;
        for (TimePeriodDto period : internetActivePeriods) {
            if (period == null) {
                throw new IncorrectScheduleException("period == null");
            }

            LocalDateTime periodStart = period.from();
            LocalDateTime periodEnd = period.to();

            if (periodStart == null || periodEnd == null || periodStart.isAfter(periodEnd)) {
                throw new IncorrectScheduleException("periodStart == null || periodEnd == null || periodStart.isAfter(periodEnd)");
            }

            if (periodStart.isBefore(from) || periodEnd.isAfter(to)) {
                throw new IncorrectScheduleException("periodStart.isBefore(from) || periodEnd.isAfter(to)");
            }

            if (previousEnd != null) {
                if (periodStart.isBefore(previousEnd)) {
                    throw new IncorrectScheduleException("periodStart.isBefore(previousEnd)");
                }
            }

            previousEnd = periodEnd;
        }
    }

}
