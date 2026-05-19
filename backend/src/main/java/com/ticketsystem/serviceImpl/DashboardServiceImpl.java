package com.ticketsystem.serviceImpl;

import com.ticketsystem.dto.DashboardStatsDTO;
import com.ticketsystem.dto.TicketResponseDTO;
import com.ticketsystem.entity.Ticket;
import com.ticketsystem.repository.EmployeeRepository;
import com.ticketsystem.repository.ProjectRepository;
import com.ticketsystem.repository.TicketRepository;
import com.ticketsystem.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final TicketRepository ticketRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public DashboardStatsDTO getStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        stats.setTotalTickets(ticketRepository.count());
        stats.setOpenTickets(ticketRepository.countByStatus("OPEN"));
        stats.setInProgressTickets(ticketRepository.countByStatus("IN_PROGRESS"));
        stats.setResolvedTickets(ticketRepository.countByStatus("RESOLVED"));
        stats.setClosedTickets(ticketRepository.countByStatus("CLOSED"));
        stats.setHighPriorityTickets(ticketRepository.countByPriority("HIGH"));
        stats.setMediumPriorityTickets(ticketRepository.countByPriority("MEDIUM"));
        stats.setLowPriorityTickets(ticketRepository.countByPriority("LOW"));
        stats.setTotalProjects(projectRepository.count());
        stats.setActiveProjects(projectRepository.countByProjectStatus("ACTIVE"));
        stats.setTotalEmployees(employeeRepository.count());
        stats.setActiveEmployees(employeeRepository.countByStatus("ACTIVE"));
        return stats;
    }

    @Override
    public List<TicketResponseDTO> getRecentTickets(int limit) {
        return ticketRepository.findAllWithDetails().stream()
                .limit(limit)
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private TicketResponseDTO toResponseDTO(Ticket ticket) {
        TicketResponseDTO dto = new TicketResponseDTO();
        dto.setId(ticket.getId());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setStatus(ticket.getStatus());
        dto.setPriority(ticket.getPriority());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());
        if (ticket.getProject() != null) {
            dto.setProjectId(ticket.getProject().getId());
            dto.setProjectName(ticket.getProject().getProjectName());
        }
        if (ticket.getAssignee() != null) {
            dto.setAssigneeId(ticket.getAssignee().getId());
            dto.setAssigneeName(ticket.getAssignee().getEmployeeName());
        }
        return dto;
    }
}
