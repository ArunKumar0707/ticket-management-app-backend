package com.ticketsystem.service;

import com.ticketsystem.dto.EmployeeProjectMappingResponseDTO;
import java.util.List;

public interface EmployeeProjectMappingService {
    void bulkAssignProjects(Long employeeId, List<Long> projectIds);
    List<EmployeeProjectMappingResponseDTO> getProjectsByEmployee(Long employeeId);
    List<EmployeeProjectMappingResponseDTO> getEmployeesByProject(Long projectId);
    void removeMapping(Long employeeId, Long projectId);
}
