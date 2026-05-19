package com.ticketsystem.controller;

import com.ticketsystem.dto.ApiResponse;
import com.ticketsystem.dto.ProjectRequestDTO;
import com.ticketsystem.dto.ProjectResponseDTO;
import com.ticketsystem.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponseDTO>>> getAllProjects() {
        return ResponseEntity.ok(ApiResponse.success("Projects retrieved", projectService.getAllProjects()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Project retrieved", projectService.getProjectById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> createProject(@Valid @RequestBody ProjectRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Project created", projectService.createProject(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> updateProject(
            @PathVariable Long id, @Valid @RequestBody ProjectRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Project updated", projectService.updateProject(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok(ApiResponse.success("Project deleted", null));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProjectResponseDTO>>> searchProjects(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.success("Search results", projectService.searchProjects(q)));
    }
}
