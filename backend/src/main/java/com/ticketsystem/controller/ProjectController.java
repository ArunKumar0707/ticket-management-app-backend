package com.ticketsystem.controller;

import com.ticketsystem.dto.ApiResponse;
import com.ticketsystem.dto.EmployeeProjectMappingResponseDTO;
import com.ticketsystem.dto.ProjectRequestDTO;
import com.ticketsystem.dto.ProjectResponseDTO;
import com.ticketsystem.entity.Project.ProjectStatus;
import com.ticketsystem.service.EmployeeProjectMappingService;
import com.ticketsystem.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProjectController {

    private final ProjectService projectService;
    private final EmployeeProjectMappingService mappingService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> createProject(@Valid @RequestBody ProjectRequestDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Project created successfully", projectService.createProject(req)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProjectResponseDTO>>> getAllProjects(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return ResponseEntity.ok(ApiResponse.success("Projects retrieved successfully",
                projectService.getAllProjects(PageRequest.of(page, size, sort))));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ProjectResponseDTO>>> getActive() {
        return ResponseEntity.ok(ApiResponse.success("Active projects", projectService.getProjectsByStatus(ProjectStatus.ACTIVE)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProjectResponseDTO>>> searchProjects(
            @RequestParam(required = false) String search, @RequestParam(required = false) ProjectStatus status,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return ResponseEntity.ok(ApiResponse.success("Search results retrieved",
                projectService.searchProjects(search, status, PageRequest.of(page, size, sort))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Project retrieved successfully", projectService.getProjectById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequestDTO req) {
        return ResponseEntity.ok(ApiResponse.success("Project updated successfully", projectService.updateProject(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok(ApiResponse.success("Project deleted successfully", null));
    }

    @GetMapping("/{id}/employees")
    public ResponseEntity<ApiResponse<List<EmployeeProjectMappingResponseDTO>>> getEmployees(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Employees retrieved", mappingService.getEmployeesByProject(id)));
    }
}
