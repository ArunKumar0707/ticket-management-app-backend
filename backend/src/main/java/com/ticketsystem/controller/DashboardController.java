package com.ticketsystem.controller;

import com.ticketsystem.dto.ApiResponse;
import com.ticketsystem.dto.DashboardStatsDTO;
import com.ticketsystem.dto.TicketResponseDTO;
import com.ticketsystem.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDTO>> getStats() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats", dashboardService.getStats()));
    }

    @GetMapping("/recent-tickets")
    public ResponseEntity<ApiResponse<List<TicketResponseDTO>>> getRecentTickets(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.success("Recent tickets", dashboardService.getRecentTickets(limit)));
    }
}
