package com.ticketsystem.service;

import com.ticketsystem.dto.SlaConfigRequestDTO;
import com.ticketsystem.dto.SlaConfigResponseDTO;
import com.ticketsystem.entity.Ticket.Priority;
import java.time.LocalDateTime;
import java.util.List;

public interface SlaConfigService {
    SlaConfigResponseDTO createSlaConfig(SlaConfigRequestDTO request);
    List<SlaConfigResponseDTO> getAllSlaConfigs();
    SlaConfigResponseDTO getSlaConfigById(Long id);
    SlaConfigResponseDTO getSlaConfigByPriority(Priority priority);
    SlaConfigResponseDTO updateSlaConfig(Long id, SlaConfigRequestDTO request);
    void deleteSlaConfig(Long id);
    LocalDateTime calculateDeadline(Priority priority, LocalDateTime start, boolean isResponse);
}
