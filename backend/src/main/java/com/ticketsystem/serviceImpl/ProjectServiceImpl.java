package com.ticketsystem.serviceImpl;

import com.ticketsystem.dto.ProjectRequestDTO;
import com.ticketsystem.dto.ProjectResponseDTO;
import com.ticketsystem.entity.Project;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.repository.ProjectRepository;
import com.ticketsystem.repository.TicketRepository;
import com.ticketsystem.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        return toResponseDTO(project);
    }

    @Override
    public ProjectResponseDTO createProject(ProjectRequestDTO dto) {
        Project project = new Project();
        mapDtoToEntity(dto, project);
        return toResponseDTO(projectRepository.save(project));
    }

    @Override
    public ProjectResponseDTO updateProject(Long id, ProjectRequestDTO dto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        mapDtoToEntity(dto, project);
        return toResponseDTO(projectRepository.save(project));
    }

    @Override
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }
        projectRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> searchProjects(String query) {
        return projectRepository.findByProjectNameContainingIgnoreCase(query).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private void mapDtoToEntity(ProjectRequestDTO dto, Project project) {
        project.setProjectName(dto.getProjectName());
        project.setProjectDescription(dto.getProjectDescription());
        project.setProjectStatus(dto.getProjectStatus() != null ? dto.getProjectStatus() : "ACTIVE");
        project.setProjectOwner(dto.getProjectOwner());
    }

    private ProjectResponseDTO toResponseDTO(Project project) {
        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setId(project.getId());
        dto.setProjectName(project.getProjectName());
        dto.setProjectDescription(project.getProjectDescription());
        dto.setProjectStatus(project.getProjectStatus());
        dto.setProjectOwner(project.getProjectOwner());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());
        dto.setTicketCount(ticketRepository.findByProjectId(project.getId()).size());
        return dto;
    }
}
