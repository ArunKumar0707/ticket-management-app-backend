package com.ticketsystem.serviceImpl;

import com.ticketsystem.dto.EmployeeRequestDTO;
import com.ticketsystem.dto.EmployeeResponseDTO;
import com.ticketsystem.entity.Employee;
import com.ticketsystem.entity.Employee.EmployeeStatus;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.repository.EmployeeRepository;
import com.ticketsystem.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO requestDTO) {
        log.info("Creating new employee: {}", requestDTO.getEmail());
        if (employeeRepository.existsByEmail(requestDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + requestDTO.getEmail());
        }
        Employee employee = Employee.builder()
                .employeeName(requestDTO.getEmployeeName())
                .email(requestDTO.getEmail())
                .designation(requestDTO.getDesignation())
                .department(requestDTO.getDepartment())
                .assignedProject(requestDTO.getAssignedProject())
                .status(requestDTO.getStatus())
                .build();
        Employee saved = employeeRepository.save(employee);
        log.info("Employee created with ID: {}", saved.getId());
        return mapToResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponseDTO> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return mapToResponseDTO(employee);
    }

    @Override
    @Transactional
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO requestDTO) {
        log.info("Updating employee with ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        if (employeeRepository.existsByEmailAndIdNot(requestDTO.getEmail(), id)) {
            throw new IllegalArgumentException("Email already exists: " + requestDTO.getEmail());
        }
        employee.setEmployeeName(requestDTO.getEmployeeName());
        employee.setEmail(requestDTO.getEmail());
        employee.setDesignation(requestDTO.getDesignation());
        employee.setDepartment(requestDTO.getDepartment());
        employee.setAssignedProject(requestDTO.getAssignedProject());
        employee.setStatus(requestDTO.getStatus());
        Employee updated = employeeRepository.save(employee);
        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        log.info("Deleting employee with ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        employeeRepository.delete(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponseDTO> searchEmployees(String search, EmployeeStatus status, Pageable pageable) {
        return employeeRepository.searchEmployees(search, status, pageable).map(this::mapToResponseDTO);
    }

    private EmployeeResponseDTO mapToResponseDTO(Employee employee) {
        return EmployeeResponseDTO.builder()
                .id(employee.getId())
                .employeeName(employee.getEmployeeName())
                .email(employee.getEmail())
                .designation(employee.getDesignation())
                .department(employee.getDepartment())
                .assignedProject(employee.getAssignedProject())
                .status(employee.getStatus())
                .userId(employee.getUserId())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}
