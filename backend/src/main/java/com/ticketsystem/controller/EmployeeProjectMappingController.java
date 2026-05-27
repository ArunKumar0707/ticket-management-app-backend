package com.ticketsystem.controller;

import com.ticketsystem.dto.ApiResponse;
import com.ticketsystem.dto.EmployeeProjectMappingResponseDTO;
import com.ticketsystem.service.EmployeeProjectMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmployeeProjectMappingController {

    private final EmployeeProjectMappingService service;

    @GetMapping("/api/employees/{employeeId}/projects")
    public ResponseEntity<ApiResponse<List<EmployeeProjectMappingResponseDTO>>> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(ApiResponse.success("Projects retrieved", service.getProjectsByEmployee(employeeId)));
    }

    @GetMapping("/api/projects/{projectId}/employees")
    public ResponseEntity<ApiResponse<List<EmployeeProjectMappingResponseDTO>>> getByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(ApiResponse.success("Employees retrieved", service.getEmployeesByProject(projectId)));
    }

    @PutMapping("/api/employees/{employeeId}/projects") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> bulkAssign(@PathVariable Long employeeId, @RequestBody List<Long> projectIds) {
        service.bulkAssignProjects(employeeId, projectIds);
        return ResponseEntity.ok(ApiResponse.success("Projects assigned", null));
    }

    @DeleteMapping("/api/employees/{employeeId}/projects/{projectId}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> remove(@PathVariable Long employeeId, @PathVariable Long projectId) {
        service.removeMapping(employeeId, projectId);
        return ResponseEntity.ok(ApiResponse.success("Mapping removed", null));
    }
}
