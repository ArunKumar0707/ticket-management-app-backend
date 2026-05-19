package com.ticketsystem.controller;

import com.ticketsystem.dto.ApiResponse;
import com.ticketsystem.dto.EmployeeRequestDTO;
import com.ticketsystem.dto.EmployeeResponseDTO;
import com.ticketsystem.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeResponseDTO>>> getAllEmployees() {
        return ResponseEntity.ok(ApiResponse.success("Employees retrieved", employeeService.getAllEmployees()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Employee retrieved", employeeService.getEmployeeById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> createEmployee(@Valid @RequestBody EmployeeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee created", employeeService.createEmployee(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> updateEmployee(
            @PathVariable Long id, @Valid @RequestBody EmployeeRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Employee updated", employeeService.updateEmployee(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee deleted", null));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<EmployeeResponseDTO>>> searchEmployees(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.success("Search results", employeeService.searchEmployees(q)));
    }
}
