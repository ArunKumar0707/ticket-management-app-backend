package com.ticketsystem.service;

import com.ticketsystem.dto.DashboardStatsDTO;
import com.ticketsystem.dto.TicketResponseDTO;

import java.util.List;

public interface DashboardService {
    DashboardStatsDTO getStats();
    List<TicketResponseDTO> getRecentTickets(int limit);
}
