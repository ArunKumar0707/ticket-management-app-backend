package com.ticketsystem.serviceImpl;

import com.ticketsystem.dto.SlaConfigRequestDTO;
import com.ticketsystem.dto.SlaConfigResponseDTO;
import com.ticketsystem.entity.SlaConfig;
import com.ticketsystem.entity.Ticket.Priority;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.repository.HolidayRepository;
import com.ticketsystem.repository.SlaConfigRepository;
import com.ticketsystem.service.SlaConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class SlaConfigServiceImpl implements SlaConfigService {

    private final SlaConfigRepository repo;
    private final HolidayRepository holidayRepo;

    @Override @Transactional
    public SlaConfigResponseDTO createSlaConfig(SlaConfigRequestDTO req) {
        if (repo.existsByPriority(req.getPriority()))
            throw new IllegalArgumentException("SLA config already exists for: " + req.getPriority());
        return map(repo.save(SlaConfig.builder()
                .priority(req.getPriority())
                .responseTimeHours(req.getResponseTimeHours())
                .resolutionTimeHours(req.getResolutionTimeHours())
                .excludeWeekends(req.getExcludeWeekends() != null ? req.getExcludeWeekends() : true)
                .excludeHolidays(req.getExcludeHolidays() != null ? req.getExcludeHolidays() : true)
                .build()));
    }

    @Override @Transactional(readOnly = true)
    public List<SlaConfigResponseDTO> getAllSlaConfigs() {
        return repo.findAll().stream().map(this::map).collect(Collectors.toList());
    }

    @Override @Transactional(readOnly = true)
    public SlaConfigResponseDTO getSlaConfigById(Long id) {
        return map(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("SLA config not found: " + id)));
    }

    @Override @Transactional(readOnly = true)
    public SlaConfigResponseDTO getSlaConfigByPriority(Priority priority) {
        return map(repo.findByPriority(priority)
                .orElseThrow(() -> new ResourceNotFoundException("SLA config not found for: " + priority)));
    }

    @Override @Transactional
    public SlaConfigResponseDTO updateSlaConfig(Long id, SlaConfigRequestDTO req) {
        SlaConfig c = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("SLA config not found: " + id));
        if (repo.existsByPriorityAndIdNot(req.getPriority(), id))
            throw new IllegalArgumentException("SLA config already exists for: " + req.getPriority());
        c.setPriority(req.getPriority());
        c.setResponseTimeHours(req.getResponseTimeHours());
        c.setResolutionTimeHours(req.getResolutionTimeHours());
        if (req.getExcludeWeekends() != null) c.setExcludeWeekends(req.getExcludeWeekends());
        if (req.getExcludeHolidays() != null) c.setExcludeHolidays(req.getExcludeHolidays());
        return map(repo.save(c));
    }

    @Override @Transactional
    public void deleteSlaConfig(Long id) {
        repo.delete(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("SLA config not found: " + id)));
    }

    /**
     * Advances business hours from startDateTime, always skipping
     * Sat/Sun and, when excludeHolidays=true, DB-configured holidays.
     */
    @Override @Transactional(readOnly = true)
    public LocalDateTime calculateDeadline(Priority priority, LocalDateTime start, boolean isResponse) {
        SlaConfig cfg = repo.findByPriority(priority).orElse(null);
        if (cfg == null) return null;
        int target = isResponse ? cfg.getResponseTimeHours() : cfg.getResolutionTimeHours();

        LocalDate windowEnd = start.toLocalDate().plusDays(90);
        Set<LocalDate> holidays = cfg.isExcludeHolidays()
                ? holidayRepo.findHolidayDatesBetween(start.toLocalDate(), windowEnd)
                        .stream().collect(Collectors.toSet())
                : Set.of();

        LocalDateTime cur = start;
        int counted = 0;
        while (counted < target) {
            cur = cur.plusHours(1);
            DayOfWeek dow = cur.getDayOfWeek();
            if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) continue;
            if (cfg.isExcludeHolidays() && holidays.contains(cur.toLocalDate())) continue;
            counted++;
        }
        return cur;
    }

    private SlaConfigResponseDTO map(SlaConfig s) {
        return SlaConfigResponseDTO.builder()
                .id(s.getId()).priority(s.getPriority())
                .responseTimeHours(s.getResponseTimeHours())
                .resolutionTimeHours(s.getResolutionTimeHours())
                .excludeWeekends(s.isExcludeWeekends())
                .excludeHolidays(s.isExcludeHolidays())
                .createdAt(s.getCreatedAt()).updatedAt(s.getUpdatedAt()).build();
    }
}
