package com.ticketsystem.serviceImpl;

import com.ticketsystem.dto.TicketRequestDTO;
import com.ticketsystem.dto.TicketResponseDTO;
import com.ticketsystem.entity.Employee;
import com.ticketsystem.entity.Project;
import com.ticketsystem.entity.Ticket;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.repository.EmployeeRepository;
import com.ticketsystem.repository.ProjectRepository;
import com.ticketsystem.repository.TicketRepository;
import com.ticketsystem.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TicketResponseDTO> getAllTickets() {
        return ticketRepository.findAllWithDetails().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TicketResponseDTO getTicketById(Long id) {
        Ticket ticket = ticketRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
        return toResponseDTO(ticket);
    }

    @Override
    public TicketResponseDTO createTicket(TicketRequestDTO dto) {
        Ticket ticket = new Ticket();
        mapDtoToEntity(dto, ticket);
        return toResponseDTO(ticketRepository.save(ticket));
    }

    @Override
    public TicketResponseDTO updateTicket(Long id, TicketRequestDTO dto) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
        mapDtoToEntity(dto, ticket);
        return toResponseDTO(ticketRepository.save(ticket));
    }

    @Override
    public void deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ticket not found with id: " + id);
        }
        ticketRepository.deleteById(id);
    }

    private void mapDtoToEntity(TicketRequestDTO dto, Ticket ticket) {
        ticket.setTitle(dto.getTitle());
        ticket.setDescription(dto.getDescription());
        ticket.setStatus(dto.getStatus() != null ? dto.getStatus() : "OPEN");
        ticket.setPriority(dto.getPriority() != null ? dto.getPriority() : "MEDIUM");

        if (dto.getProjectId() != null) {
            Project project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + dto.getProjectId()));
            ticket.setProject(project);
        } else {
            ticket.setProject(null);
        }

        if (dto.getAssigneeId() != null) {
            Employee employee = employeeRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + dto.getAssigneeId()));
            ticket.setAssignee(employee);
        } else {
            ticket.setAssignee(null);
        }
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
