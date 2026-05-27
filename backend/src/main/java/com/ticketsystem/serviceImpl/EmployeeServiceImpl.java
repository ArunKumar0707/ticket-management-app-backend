package com.ticketsystem.serviceImpl;

import com.ticketsystem.dto.EmployeeRequestDTO;
import com.ticketsystem.dto.EmployeeResponseDTO;
import com.ticketsystem.entity.Employee;
import com.ticketsystem.entity.Employee.EmployeeStatus;
import com.ticketsystem.entity.ShiftHours;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.repository.EmployeeRepository;
import com.ticketsystem.repository.ShiftHoursRepository;
import com.ticketsystem.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor @Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ShiftHoursRepository shiftHoursRepository;

    @Override @Transactional
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO req) {
        log.info("Creating employee: {}", req.getEmail());
        if (employeeRepository.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email already exists: " + req.getEmail());
        return mapToDTO(employeeRepository.save(Employee.builder()
                .employeeCode(req.getEmployeeCode())
                .employeeName(req.getEmployeeName())
                .email(req.getEmail())
                .designation(req.getDesignation())
                .department(req.getDepartment())
                .assignedProject(req.getAssignedProject())
                .shiftId(req.getShiftId())
                .status(req.getStatus())
                .build()));
    }

    @Override @Transactional(readOnly = true)
    public Page<EmployeeResponseDTO> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable).map(this::mapToDTO);
    }

    @Override @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(Long id) {
        return mapToDTO(employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id)));
    }

    @Override @Transactional
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO req) {
        Employee e = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
        if (employeeRepository.existsByEmailAndIdNot(req.getEmail(), id))
            throw new IllegalArgumentException("Email already exists: " + req.getEmail());
        e.setEmployeeCode(req.getEmployeeCode());
        e.setEmployeeName(req.getEmployeeName());
        e.setEmail(req.getEmail());
        e.setDesignation(req.getDesignation());
        e.setDepartment(req.getDepartment());
        e.setAssignedProject(req.getAssignedProject());
        e.setShiftId(req.getShiftId());
        e.setStatus(req.getStatus());
        return mapToDTO(employeeRepository.save(e));
    }

    @Override @Transactional
    public void deleteEmployee(Long id) {
        employeeRepository.delete(employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id)));
    }

    @Override @Transactional(readOnly = true)
    public Page<EmployeeResponseDTO> searchEmployees(String search, EmployeeStatus status, Pageable pageable) {
        return employeeRepository.searchEmployees(search, status, pageable).map(this::mapToDTO);
    }

    private EmployeeResponseDTO mapToDTO(Employee e) {
        String shiftName = null;
        if (e.getShiftId() != null)
            shiftName = shiftHoursRepository.findById(e.getShiftId()).map(ShiftHours::getShiftName).orElse(null);
        return EmployeeResponseDTO.builder()
                .id(e.getId()).employeeCode(e.getEmployeeCode())
                .employeeName(e.getEmployeeName()).email(e.getEmail())
                .designation(e.getDesignation()).department(e.getDepartment())
                .assignedProject(e.getAssignedProject())
                .shiftId(e.getShiftId()).shiftName(shiftName)
                .status(e.getStatus()).userId(e.getUserId())
                .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }
}
