package com.ticketsystem.serviceImpl;

import com.ticketsystem.dto.EmployeeRequestDTO;
import com.ticketsystem.dto.EmployeeResponseDTO;
import com.ticketsystem.dto.EmployeeResponseDTO.ProjectSummaryDTO;
import com.ticketsystem.entity.Employee;
import com.ticketsystem.entity.Employee.EmployeeStatus;
import com.ticketsystem.entity.Project;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.repository.EmployeeRepository;
import com.ticketsystem.repository.ProjectRepository;
import com.ticketsystem.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ProjectRepository  projectRepository;
    private final PasswordEncoder    passwordEncoder;

    @Override
    @Transactional
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO dto) {
        log.info("Creating employee: {}", dto.getEmail());
        if (employeeRepository.existsByEmail(dto.getEmail()))
            throw new IllegalArgumentException("Email already exists: " + dto.getEmail());
        if (employeeRepository.existsByUsername(dto.getUsername()))
            throw new IllegalArgumentException("Username already exists: " + dto.getUsername());

        Employee emp = Employee.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .employeeName(dto.getEmployeeName())
                .designation(dto.getDesignation())
                .department(dto.getDepartment())
                .status(dto.getStatus())
                .isActive(true)
                .projects(resolveProjects(dto.getProjectIds()))
                .build();

        return mapToDTO(employeeRepository.save(emp));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponseDTO> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable).map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(Long id) {
        return mapToDTO(findOrThrow(id));
    }

    @Override
    @Transactional
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO dto) {
        Employee emp = findOrThrow(id);
        if (employeeRepository.existsByEmailAndIdNot(dto.getEmail(), id))
            throw new IllegalArgumentException("Email already in use: " + dto.getEmail());
        if (employeeRepository.existsByUsernameAndIdNot(dto.getUsername(), id))
            throw new IllegalArgumentException("Username already in use: " + dto.getUsername());

        emp.setUsername(dto.getUsername());
        emp.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank())
            emp.setPassword(passwordEncoder.encode(dto.getPassword()));
        emp.setRole(dto.getRole());
        emp.setEmployeeName(dto.getEmployeeName());
        emp.setDesignation(dto.getDesignation());
        emp.setDepartment(dto.getDepartment());
        emp.setStatus(dto.getStatus());
        emp.setProjects(resolveProjects(dto.getProjectIds()));

        return mapToDTO(employeeRepository.save(emp));
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        employeeRepository.delete(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponseDTO> searchEmployees(String search, EmployeeStatus status, Pageable pageable) {
        return employeeRepository.searchEmployees(search, status, pageable).map(this::mapToDTO);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Employee findOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
    }

    private Set<Project> resolveProjects(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();
        return new HashSet<>(projectRepository.findAllById(ids));
    }

    private EmployeeResponseDTO mapToDTO(Employee e) {
        List<ProjectSummaryDTO> projects = e.getProjects() == null
                ? Collections.emptyList()
                : e.getProjects().stream()
                    .map(p -> ProjectSummaryDTO.builder()
                            .id(p.getId())
                            .projectCode(p.getProjectCode())
                            .projectName(p.getProjectName())
                            .build())
                    .collect(Collectors.toList());

        return EmployeeResponseDTO.builder()
                .id(e.getId())
                .username(e.getUsername())
                .email(e.getEmail())
                .role(e.getRole())
                .employeeName(e.getEmployeeName())
                .designation(e.getDesignation())
                .department(e.getDepartment())
                .status(e.getStatus())
                .isActive(e.isActive())
                .projects(projects)
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
