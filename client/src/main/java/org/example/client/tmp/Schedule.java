package org.example.client.tmp;

import org.example.client.exception.IncorrectScheduleException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record Schedule(
        LocalDateTime start,
        LocalDateTime end,
        List<TimePeriod> internetActivePeriods
) {

    public Schedule(LocalDateTime start, LocalDateTime end, List<TimePeriod> internetActivePeriods) {
        if (internetActivePeriods == null) {
            internetActivePeriods = new ArrayList<>();
        }

        this.start = start;
        this.end = end;
        this.internetActivePeriods = internetActivePeriods;

        validation();
    }

    public void validation() {
        if (start == null || end == null || start.isAfter(end)) {
            throw new IncorrectScheduleException("start == null || end == null || start.isAfter(end)");
        }

        LocalDateTime previousEnd = null;
        for (TimePeriod period : internetActivePeriods) {
            if (period == null) {
                throw new IncorrectScheduleException("period == null");
            }

            LocalDateTime periodStart = period.start();
            LocalDateTime periodEnd = period.end();

            if (periodStart == null || periodEnd == null || periodStart.isAfter(periodEnd)) {
                throw new IncorrectScheduleException("periodStart == null || periodEnd == null || periodStart.isAfter(periodEnd)");
            }

            if (periodStart.isBefore(start) || periodEnd.isAfter(end)) {
                throw new IncorrectScheduleException("periodStart.isBefore(start) || periodEnd.isAfter(end)");
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
