package com.ticketsystem.serviceImpl;

import com.ticketsystem.dto.DashboardStatsDTO;
import com.ticketsystem.dto.TicketRequestDTO;
import com.ticketsystem.dto.TicketResponseDTO;
import com.ticketsystem.entity.Ticket;
import com.ticketsystem.entity.Ticket.CurrentStatus;
import com.ticketsystem.entity.Ticket.Priority;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.repository.TicketRepository;
import com.ticketsystem.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    @Override
    @Transactional
    public TicketResponseDTO createTicket(TicketRequestDTO requestDTO) {
        log.info("Creating new ticket for project: {}", requestDTO.getProjectAssignment());

        String ticketId = generateTicketId();

        Ticket ticket = Ticket.builder()
                .ticketId(ticketId)
                .projectAssignment(requestDTO.getProjectAssignment())
                .issueDescription(requestDTO.getIssueDescription())
                .assignedEmployee(requestDTO.getAssignedEmployee())
                .supportLevel(requestDTO.getSupportLevel())
                .priority(requestDTO.getPriority())
                .generationDateTime(requestDTO.getGenerationDateTime())
                .responseDateTime(requestDTO.getResponseDateTime())
                .resolutionTime(requestDTO.getResolutionTime())
                .currentStatus(requestDTO.getCurrentStatus())
                .resolutionDetails(requestDTO.getResolutionDetails())
                .remarks(requestDTO.getRemarks())
                .build();

        Ticket saved = ticketRepository.save(ticket);
        log.info("Ticket created with ID: {}", saved.getTicketId());
        return mapToResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketResponseDTO> getAllTickets(Pageable pageable) {
        log.info("Fetching all tickets, page: {}", pageable.getPageNumber());
        return ticketRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public TicketResponseDTO getTicketById(Long id) {
        log.info("Fetching ticket by ID: {}", id);
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
        return mapToResponseDTO(ticket);
    }

    @Override
    @Transactional
    public TicketResponseDTO updateTicket(Long id, TicketRequestDTO requestDTO) {
        log.info("Updating ticket with ID: {}", id);
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));

        ticket.setProjectAssignment(requestDTO.getProjectAssignment());
        ticket.setIssueDescription(requestDTO.getIssueDescription());
        ticket.setAssignedEmployee(requestDTO.getAssignedEmployee());
        ticket.setSupportLevel(requestDTO.getSupportLevel());
        ticket.setPriority(requestDTO.getPriority());
        ticket.setGenerationDateTime(requestDTO.getGenerationDateTime());
        ticket.setResponseDateTime(requestDTO.getResponseDateTime());
        ticket.setResolutionTime(requestDTO.getResolutionTime());
        ticket.setCurrentStatus(requestDTO.getCurrentStatus());
        ticket.setResolutionDetails(requestDTO.getResolutionDetails());
        ticket.setRemarks(requestDTO.getRemarks());

        Ticket updated = ticketRepository.save(ticket);
        log.info("Ticket updated: {}", updated.getTicketId());
        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteTicket(Long id) {
        log.info("Deleting ticket with ID: {}", id);
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
        ticketRepository.delete(ticket);
        log.info("Ticket deleted with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketResponseDTO> searchTickets(
            String ticketId,
            String projectAssignment,
            CurrentStatus status,
            Priority priority,
            Pageable pageable) {
        log.info("Searching tickets with filters - ticketId: {}, project: {}, status: {}, priority: {}",
                ticketId, projectAssignment, status, priority);
        return ticketRepository.searchTickets(ticketId, projectAssignment, status, priority, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats() {
        long total = ticketRepository.count();
        long open = ticketRepository.countByCurrentStatus(CurrentStatus.OPEN);
        long inProgress = ticketRepository.countByCurrentStatus(CurrentStatus.IN_PROGRESS);
        long resolved = ticketRepository.countByCurrentStatus(CurrentStatus.RESOLVED);
        long closed = ticketRepository.countByCurrentStatus(CurrentStatus.CLOSED);

        return DashboardStatsDTO.builder()
                .totalTickets(total)
                .openTickets(open)
                .inProgressTickets(inProgress)
                .resolvedTickets(resolved)
                .closedTickets(closed)
                .build();
    }

    private String generateTicketId() {
        Integer maxSeq = ticketRepository.findMaxTicketSequence();
        int nextSeq = (maxSeq == null ? 1000 : maxSeq) + 1;
        String ticketId = "INC-" + nextSeq;
        // Ensure uniqueness
        while (ticketRepository.existsByTicketId(ticketId)) {
            nextSeq++;
            ticketId = "INC-" + nextSeq;
        }
        return ticketId;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketResponseDTO> getTicketsByAssignee(String assignedEmployee, CurrentStatus status, Pageable pageable) {
        return ticketRepository.findByAssignedEmployee(assignedEmployee, status, pageable)
                .map(this::mapToResponseDTO);
    }

    private TicketResponseDTO mapToResponseDTO(Ticket ticket) {
        return TicketResponseDTO.builder()
                .id(ticket.getId())
                .ticketId(ticket.getTicketId())
                .projectAssignment(ticket.getProjectAssignment())
                .issueDescription(ticket.getIssueDescription())
                .assignedEmployee(ticket.getAssignedEmployee())
                .supportLevel(ticket.getSupportLevel())
                .priority(ticket.getPriority())
                .generationDateTime(ticket.getGenerationDateTime())
                .responseDateTime(ticket.getResponseDateTime())
                .resolutionTime(ticket.getResolutionTime())
                .currentStatus(ticket.getCurrentStatus())
                .resolutionDetails(ticket.getResolutionDetails())
                .remarks(ticket.getRemarks())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }
}
