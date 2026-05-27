package com.ticketsystem.controller;

import com.ticketsystem.dto.ApiResponse;
import com.ticketsystem.dto.DashboardStatsDTO;
import com.ticketsystem.dto.TicketRequestDTO;
import com.ticketsystem.dto.TicketResponseDTO;
import com.ticketsystem.entity.Ticket.CurrentStatus;
import com.ticketsystem.entity.Ticket.Priority;
import com.ticketsystem.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class TicketController {

    private final TicketService ticketService;

    // POST /api/tickets - Create Ticket
    @PostMapping
    public ResponseEntity<ApiResponse<TicketResponseDTO>> createTicket(
            @Valid @RequestBody TicketRequestDTO requestDTO) {

        TicketResponseDTO response =
                ticketService.createTicket(requestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Ticket created successfully",
                        response
                ));
    }

    // GET /api/tickets - Get All Tickets (paginated)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TicketResponseDTO>>> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable =
                PageRequest.of(page, size, sort);

        Page<TicketResponseDTO> tickets =
                ticketService.getAllTickets(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Tickets retrieved successfully",
                        tickets
                )
        );
    }

    // GET /api/tickets/dashboard - Dashboard Stats
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsDTO>> getDashboardStats() {

        DashboardStatsDTO stats =
                ticketService.getDashboardStats();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Dashboard stats retrieved",
                        stats
                )
        );
    }

    // GET /api/tickets/search - Search Tickets
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<TicketResponseDTO>>> searchTickets(
            @RequestParam(required = false) String ticketId,
            @RequestParam(required = false) String projectAssignment,
            @RequestParam(required = false) CurrentStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable =
                PageRequest.of(page, size, sort);

        Page<TicketResponseDTO> tickets =
                ticketService.searchTickets(
                        ticketId,
                        projectAssignment,
                        status,
                        priority,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Search results retrieved",
                        tickets
                )
        );
    }

    // GET /api/tickets/{id} - Get Ticket By ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketResponseDTO>> getTicketById(
            @PathVariable Long id) {

        TicketResponseDTO ticket =
                ticketService.getTicketById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Ticket retrieved successfully",
                        ticket
                )
        );
    }

    // PUT /api/tickets/{id} - Update Ticket
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketResponseDTO>> updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody TicketRequestDTO requestDTO) {

        TicketResponseDTO updated =
                ticketService.updateTicket(id, requestDTO);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Ticket updated successfully",
                        updated
                )
        );
    }

    // GET /api/tickets/my-tickets
    @GetMapping("/my-tickets")
    public ResponseEntity<ApiResponse<Page<TicketResponseDTO>>> getMyTickets(
            Principal principal,
            @RequestParam(required = false) CurrentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable =
                PageRequest.of(page, size, sort);

        String assigneeName =
                principal != null
                        ? principal.getName()
                        : "";

        Page<TicketResponseDTO> tickets =
                ticketService.getTicketsByAssignee(
                        assigneeName,
                        status,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "My tickets retrieved",
                        tickets
                )
        );
    }

    // DELETE /api/tickets/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTicket(
            @PathVariable Long id) {

        ticketService.deleteTicket(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Ticket deleted successfully",
                        null
                )
        );
    }
}