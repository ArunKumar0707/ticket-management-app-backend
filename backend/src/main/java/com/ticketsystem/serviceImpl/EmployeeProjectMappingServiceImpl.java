package com.ticketsystem.serviceImpl;

import com.ticketsystem.dto.EmployeeProjectMappingResponseDTO;
import com.ticketsystem.entity.Employee;
import com.ticketsystem.entity.EmployeeProjectMapping;
import com.ticketsystem.entity.Project;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.repository.EmployeeProjectMappingRepository;
import com.ticketsystem.repository.EmployeeRepository;
import com.ticketsystem.repository.ProjectRepository;
import com.ticketsystem.service.EmployeeProjectMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class EmployeeProjectMappingServiceImpl implements EmployeeProjectMappingService {

    private final EmployeeProjectMappingRepository mappingRepo;
    private final EmployeeRepository employeeRepo;
    private final ProjectRepository projectRepo;

    @Override @Transactional
    public void bulkAssignProjects(Long employeeId, List<Long> projectIds) {
        employeeRepo.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));

        // Deactivate all existing
        List<EmployeeProjectMapping> existing = mappingRepo.findAllByEmployeeId(employeeId);
        existing.forEach(m -> m.setActive(false));
        mappingRepo.saveAll(existing);

        // Activate or create
        for (Long pid : projectIds) {
            projectRepo.findById(pid).orElseThrow(() -> new ResourceNotFoundException("Project not found: " + pid));
            mappingRepo.findByEmployeeIdAndProjectId(employeeId, pid).ifPresentOrElse(
                m -> { m.setActive(true); mappingRepo.save(m); },
                () -> mappingRepo.save(EmployeeProjectMapping.builder()
                        .employeeId(employeeId).projectId(pid)
                        .assignedFrom(LocalDate.now()).isActive(true).build())
            );
        }
    }

    @Override @Transactional(readOnly = true)
    public List<EmployeeProjectMappingResponseDTO> getProjectsByEmployee(Long employeeId) {
        return mappingRepo.findByEmployeeIdAndIsActiveTrue(employeeId).stream()
                .map(m -> {
                    Employee emp = employeeRepo.findById(m.getEmployeeId()).orElse(null);
                    Project proj = projectRepo.findById(m.getProjectId()).orElse(null);
                    return mapDTO(m, emp, proj);
                }).collect(Collectors.toList());
    }

    @Override @Transactional(readOnly = true)
    public List<EmployeeProjectMappingResponseDTO> getEmployeesByProject(Long projectId) {
        return mappingRepo.findByProjectIdAndIsActiveTrue(projectId).stream()
                .map(m -> {
                    Employee emp = employeeRepo.findById(m.getEmployeeId()).orElse(null);
                    Project proj = projectRepo.findById(m.getProjectId()).orElse(null);
                    return mapDTO(m, emp, proj);
                }).collect(Collectors.toList());
    }

    @Override @Transactional
    public void removeMapping(Long employeeId, Long projectId) {
        EmployeeProjectMapping m = mappingRepo.findByEmployeeIdAndProjectId(employeeId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Mapping not found"));
        m.setActive(false);
        mappingRepo.save(m);
    }

    private EmployeeProjectMappingResponseDTO mapDTO(EmployeeProjectMapping m, Employee emp, Project proj) {
        return EmployeeProjectMappingResponseDTO.builder()
                .id(m.getId()).employeeId(m.getEmployeeId())
                .employeeName(emp != null ? emp.getEmployeeName() : null)
                .projectId(m.getProjectId())
                .projectName(proj != null ? proj.getProjectName() : null)
                .projectCode(proj != null ? proj.getProjectCode() : null)
                .assignedFrom(m.getAssignedFrom()).assignedTo(m.getAssignedTo())
                .isActive(m.isActive()).createdAt(m.getCreatedAt()).build();
    }
}
