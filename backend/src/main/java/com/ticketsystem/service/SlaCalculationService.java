package com.ticketsystem.service;

import java.time.LocalDateTime;

/**
 * Calculates working-hours durations between two timestamps,
 * honouring shift hours, weekends and configured holidays.
 */
public interface SlaCalculationService {
    /**
     * @return working minutes between start and end
     */
    long workingMinutesBetween(LocalDateTime start, LocalDateTime end);

    /**
     * @return the deadline LocalDateTime that is N working hours after start
     */
    LocalDateTime addWorkingHours(LocalDateTime start, double workingHours);

    /**
     * @return true if the ticket with the given priority + supportLevel is breached
     */
    boolean isSlaBreached(String priority, String supportLevel, LocalDateTime createdAt);
}
