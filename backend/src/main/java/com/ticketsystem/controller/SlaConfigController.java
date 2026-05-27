package com.ticketsystem.controller;

import com.ticketsystem.dto.ApiResponse;
import com.ticketsystem.dto.SlaConfigRequestDTO;
import com.ticketsystem.dto.SlaConfigResponseDTO;
import com.ticketsystem.entity.Ticket.Priority;
import com.ticketsystem.service.SlaConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/config/sla")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SlaConfigController {

    private final SlaConfigService service;

    @PostMapping @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SlaConfigResponseDTO>> create(@Valid @RequestBody SlaConfigRequestDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("SLA config created", service.createSlaConfig(req)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SlaConfigResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("SLA configs retrieved", service.getAllSlaConfigs()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SlaConfigResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("SLA config retrieved", service.getSlaConfigById(id)));
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<ApiResponse<SlaConfigResponseDTO>> getByPriority(@PathVariable Priority priority) {
        return ResponseEntity.ok(ApiResponse.success("SLA config retrieved", service.getSlaConfigByPriority(priority)));
    }

    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SlaConfigResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody SlaConfigRequestDTO req) {
        return ResponseEntity.ok(ApiResponse.success("SLA config updated", service.updateSlaConfig(id, req)));
    }

    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.deleteSlaConfig(id);
        return ResponseEntity.ok(ApiResponse.success("SLA config deleted", null));
    }
}
