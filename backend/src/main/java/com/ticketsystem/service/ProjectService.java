package com.ticketsystem.service;

import com.ticketsystem.dto.ProjectRequestDTO;
import com.ticketsystem.dto.ProjectResponseDTO;

import java.util.List;

public interface ProjectService {
    List<ProjectResponseDTO> getAllProjects();
    ProjectResponseDTO getProjectById(Long id);
    ProjectResponseDTO createProject(ProjectRequestDTO dto);
    ProjectResponseDTO updateProject(Long id, ProjectRequestDTO dto);
    void deleteProject(Long id);
    List<ProjectResponseDTO> searchProjects(String query);
}
