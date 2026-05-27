package com.ticketsystem.serviceImpl;

import com.ticketsystem.entity.Holiday;
import com.ticketsystem.entity.ShiftHours;
import com.ticketsystem.entity.SlaConfig;
import com.ticketsystem.repository.HolidayRepository;
import com.ticketsystem.repository.ShiftHoursRepository;
import com.ticketsystem.repository.SlaConfigRepository;
import com.ticketsystem.service.SlaCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlaCalculationServiceImpl implements SlaCalculationService {

    private final ShiftHoursRepository shiftHoursRepository;
    private final HolidayRepository    holidayRepository;
    private final SlaConfigRepository  slaConfigRepository;

    /**
     * Counts working minutes between two datetimes.
     * Skips weekends (Sat/Sun) and configured holidays.
     * Only counts time that falls within at least one active shift.
     */
    @Override
    @Transactional(readOnly = true)
    public long workingMinutesBetween(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) return 0;

        List<ShiftHours> shifts = shiftHoursRepository.findByIsActiveTrue();
        if (shifts.isEmpty()) {
            // If no shifts configured, count all non-weekend / non-holiday minutes
            return rawMinutesBetween(start, end);
        }

        Set<LocalDate> holidays = fetchHolidayDates(start.toLocalDate(), end.toLocalDate());

        long totalMinutes = 0;
        LocalDateTime cursor = start;

        while (cursor.isBefore(end)) {
            LocalDate day = cursor.toLocalDate();

            // Skip weekends and holidays
            if (isNonWorkingDay(day, holidays)) {
                cursor = day.plusDays(1).atTime(LocalTime.MIN);
                continue;
            }

            // Add shift minutes that overlap with this day's [cursor, end]
            for (ShiftHours shift : shifts) {
                LocalDateTime shiftStart = day.atTime(shift.getStartTime());
                LocalDateTime shiftEnd   = day.atTime(shift.getEndTime());

                LocalDateTime overlapStart = cursor.isAfter(shiftStart) ? cursor : shiftStart;
                LocalDateTime overlapEnd   = end.isBefore(shiftEnd)     ? end    : shiftEnd;

                if (overlapStart.isBefore(overlapEnd)) {
                    totalMinutes += ChronoUnit.MINUTES.between(overlapStart, overlapEnd);
                }
            }

            cursor = day.plusDays(1).atTime(LocalTime.MIN);
        }

        return totalMinutes;
    }

    /**
     * Returns a future deadline that is exactly {@code workingHours} working-hours
     * after {@code start}, honouring shift windows, weekends and holidays.
     */
    @Override
    @Transactional(readOnly = true)
    public LocalDateTime addWorkingHours(LocalDateTime start, double workingHours) {
        long remainingMinutes = (long) (workingHours * 60);
        List<ShiftHours> shifts = shiftHoursRepository.findByIsActiveTrue();

        if (shifts.isEmpty()) {
            // Fall back: treat every non-weekend / non-holiday minute as working
            return addRawWorkingMinutes(start, remainingMinutes);
        }

        LocalDate endHorizon = start.toLocalDate().plusYears(1);
        Set<LocalDate> holidays = fetchHolidayDates(start.toLocalDate(), endHorizon);

        LocalDateTime cursor = start;

        while (remainingMinutes > 0) {
            LocalDate day = cursor.toLocalDate();

            if (isNonWorkingDay(day, holidays)) {
                cursor = day.plusDays(1).atTime(shifts.get(0).getStartTime());
                continue;
            }

            for (ShiftHours shift : shifts) {
                if (remainingMinutes <= 0) break;

                LocalDateTime shiftStart = day.atTime(shift.getStartTime());
                LocalDateTime shiftEnd   = day.atTime(shift.getEndTime());

                if (cursor.isBefore(shiftStart)) cursor = shiftStart;
                if (!cursor.isBefore(shiftEnd))  continue;

                long available = ChronoUnit.MINUTES.between(cursor, shiftEnd);
                if (available >= remainingMinutes) {
                    return cursor.plusMinutes(remainingMinutes);
                }
                remainingMinutes -= available;
                cursor = shiftEnd;
            }

            // Move to next day's first shift start
            cursor = day.plusDays(1).atTime(shifts.get(0).getStartTime());
        }

        return cursor;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSlaBreached(String priority, String supportLevel, LocalDateTime createdAt) {
        Optional<SlaConfig> cfg = slaConfigRepository
                .findByPriorityAndSupportLevel(priority, supportLevel);
        if (cfg.isEmpty()) return false;

        LocalDateTime deadline = addWorkingHours(createdAt, cfg.get().getResolutionTimeHours());
        return LocalDateTime.now().isAfter(deadline);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private boolean isNonWorkingDay(LocalDate day, Set<LocalDate> holidays) {
        DayOfWeek dow = day.getDayOfWeek();
        return dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY || holidays.contains(day);
    }

    private Set<LocalDate> fetchHolidayDates(LocalDate from, LocalDate to) {
        return holidayRepository.findByHolidayDateBetween(from, to)
                .stream()
                .map(Holiday::getHolidayDate)
                .collect(Collectors.toSet());
    }

    private long rawMinutesBetween(LocalDateTime start, LocalDateTime end) {
        // Count only non-weekend, non-holiday minutes without shift restrictions
        Set<LocalDate> holidays = fetchHolidayDates(start.toLocalDate(), end.toLocalDate());
        long total = 0;
        LocalDateTime cursor = start;
        while (cursor.isBefore(end)) {
            if (!isNonWorkingDay(cursor.toLocalDate(), holidays)) {
                total++;
            }
            cursor = cursor.plusMinutes(1);
            if (total > 525_600) break; // safety cap: 1 year of minutes
        }
        return total;
    }

    private LocalDateTime addRawWorkingMinutes(LocalDateTime start, long minutes) {
        LocalDate endHorizon = start.toLocalDate().plusYears(1);
        Set<LocalDate> holidays = fetchHolidayDates(start.toLocalDate(), endHorizon);
        LocalDateTime cursor = start;
        long added = 0;
        while (added < minutes) {
            cursor = cursor.plusMinutes(1);
            if (!isNonWorkingDay(cursor.toLocalDate(), holidays)) added++;
        }
        return cursor;
    }
}
