package com.ticketsystem.controller;

import com.ticketsystem.dto.ApiResponse;
import com.ticketsystem.dto.EmployeeRequestDTO;
import com.ticketsystem.dto.EmployeeResponseDTO;
import com.ticketsystem.entity.Employee.EmployeeStatus;
import com.ticketsystem.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class EmployeeController {

    private final EmployeeService employeeService;

    // POST /api/employees - Create Employee
    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> createEmployee(
            @Valid @RequestBody EmployeeRequestDTO requestDTO) {
        EmployeeResponseDTO response = employeeService.createEmployee(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee created successfully", response));
    }

    // GET /api/employees - Get All Employees (paginated)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<EmployeeResponseDTO>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<EmployeeResponseDTO> employees = employeeService.getAllEmployees(pageable);
        return ResponseEntity.ok(ApiResponse.success("Employees retrieved successfully", employees));
    }

    // GET /api/employees/search - Search Employees
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<EmployeeResponseDTO>>> searchEmployees(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) EmployeeStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<EmployeeResponseDTO> employees = employeeService.searchEmployees(search, status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search results retrieved", employees));
    }

    // GET /api/employees/{id} - Get Employee By ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> getEmployeeById(@PathVariable Long id) {
        EmployeeResponseDTO employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(ApiResponse.success("Employee retrieved successfully", employee));
    }

    // PUT /api/employees/{id} - Update Employee
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDTO requestDTO) {
        EmployeeResponseDTO updated = employeeService.updateEmployee(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Employee updated successfully", updated));
    }

    // DELETE /api/employees/{id} - Delete Employee
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee deleted successfully", null));
    }
}
