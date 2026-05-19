package com.ticketsystem.service;

import com.ticketsystem.dto.TicketRequestDTO;
import com.ticketsystem.dto.TicketResponseDTO;

import java.util.List;

public interface TicketService {
    List<TicketResponseDTO> getAllTickets();
    TicketResponseDTO getTicketById(Long id);
    TicketResponseDTO createTicket(TicketRequestDTO dto);
    TicketResponseDTO updateTicket(Long id, TicketRequestDTO dto);
    void deleteTicket(Long id);
}
