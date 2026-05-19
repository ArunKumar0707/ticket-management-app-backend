package com.ticketsystem.controller;

import com.ticketsystem.dto.ApiResponse;
import com.ticketsystem.dto.TicketRequestDTO;
import com.ticketsystem.dto.TicketResponseDTO;
import com.ticketsystem.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TicketResponseDTO>>> getAllTickets() {
        List<TicketResponseDTO> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(ApiResponse.success("Tickets retrieved successfully", tickets));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketResponseDTO>> getTicketById(@PathVariable Long id) {
        TicketResponseDTO ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ApiResponse.success("Ticket retrieved successfully", ticket));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TicketResponseDTO>> createTicket(@Valid @RequestBody TicketRequestDTO dto) {
        TicketResponseDTO created = ticketService.createTicket(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Ticket created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketResponseDTO>> updateTicket(
            @PathVariable Long id, @Valid @RequestBody TicketRequestDTO dto) {
        TicketResponseDTO updated = ticketService.updateTicket(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Ticket updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.ok(ApiResponse.success("Ticket deleted successfully", null));
    }
}
