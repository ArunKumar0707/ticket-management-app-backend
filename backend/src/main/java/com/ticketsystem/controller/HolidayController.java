package com.ticketsystem.controller;

import com.ticketsystem.dto.ApiResponse;
import com.ticketsystem.dto.HolidayRequestDTO;
import com.ticketsystem.dto.HolidayResponseDTO;
import com.ticketsystem.service.HolidayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/config/holidays")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HolidayController {

    private final HolidayService service;

    @PostMapping @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<HolidayResponseDTO>> create(@Valid @RequestBody HolidayRequestDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Holiday created", service.createHoliday(req)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<HolidayResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success("Holidays retrieved",
                service.getAllHolidays(PageRequest.of(page, size, Sort.by("holidayDate").ascending()))));
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<ApiResponse<List<HolidayResponseDTO>>> getByYear(@PathVariable int year) {
        return ResponseEntity.ok(ApiResponse.success("Holidays for " + year, service.getHolidaysByYear(year)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HolidayResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Holiday retrieved", service.getHolidayById(id)));
    }

    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<HolidayResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody HolidayRequestDTO req) {
        return ResponseEntity.ok(ApiResponse.success("Holiday updated", service.updateHoliday(id, req)));
    }

    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.deleteHoliday(id);
        return ResponseEntity.ok(ApiResponse.success("Holiday deleted", null));
    }
}
