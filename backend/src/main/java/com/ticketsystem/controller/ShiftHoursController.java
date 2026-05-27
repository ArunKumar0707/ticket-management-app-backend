package com.ticketsystem.controller;

import com.ticketsystem.dto.ApiResponse;
import com.ticketsystem.dto.ShiftHoursRequestDTO;
import com.ticketsystem.dto.ShiftHoursResponseDTO;
import com.ticketsystem.service.ShiftHoursService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/config/shifts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ShiftHoursController {

    private final ShiftHoursService service;

    @PostMapping @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ShiftHoursResponseDTO>> create(@Valid @RequestBody ShiftHoursRequestDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Shift created", service.createShift(req)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ShiftHoursResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success("Shifts retrieved",
                service.getAllShifts(PageRequest.of(page, size, Sort.by("shiftName").ascending()))));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ShiftHoursResponseDTO>>> getActive() {
        return ResponseEntity.ok(ApiResponse.success("Active shifts", service.getActiveShifts()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShiftHoursResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Shift retrieved", service.getShiftById(id)));
    }

    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ShiftHoursResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody ShiftHoursRequestDTO req) {
        return ResponseEntity.ok(ApiResponse.success("Shift updated", service.updateShift(id, req)));
    }

    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.deleteShift(id);
        return ResponseEntity.ok(ApiResponse.success("Shift deactivated", null));
    }
}
