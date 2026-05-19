package com.ticketsystem.serviceImpl;

import com.ticketsystem.dto.EmployeeRequestDTO;
import com.ticketsystem.dto.EmployeeResponseDTO;
import com.ticketsystem.entity.Employee;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.repository.EmployeeRepository;
import com.ticketsystem.repository.TicketRepository;
import com.ticketsystem.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return toResponseDTO(employee);
    }

    @Override
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO dto) {
        if (employeeRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + dto.getEmail());
        }
        Employee employee = new Employee();
        mapDtoToEntity(dto, employee);
        return toResponseDTO(employeeRepository.save(employee));
    }

    @Override
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        if (employeeRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new IllegalArgumentException("Email already in use by another employee");
        }
        mapDtoToEntity(dto, employee);
        return toResponseDTO(employeeRepository.save(employee));
    }

    @Override
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> searchEmployees(String query) {
        return employeeRepository
                .findByEmployeeNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query)
                .stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    private void mapDtoToEntity(EmployeeRequestDTO dto, Employee employee) {
        employee.setEmployeeName(dto.getEmployeeName());
        employee.setEmail(dto.getEmail());
        employee.setPhoneNumber(dto.getPhoneNumber());
        employee.setDepartment(dto.getDepartment());
        employee.setRole(dto.getRole());
        employee.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");
    }

    private EmployeeResponseDTO toResponseDTO(Employee employee) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(employee.getId());
        dto.setEmployeeName(employee.getEmployeeName());
        dto.setEmail(employee.getEmail());
        dto.setPhoneNumber(employee.getPhoneNumber());
        dto.setDepartment(employee.getDepartment());
        dto.setRole(employee.getRole());
        dto.setStatus(employee.getStatus());
        dto.setCreatedAt(employee.getCreatedAt());
        dto.setUpdatedAt(employee.getUpdatedAt());
        dto.setAssignedTicketCount(ticketRepository.findByAssigneeId(employee.getId()).size());
        return dto;
    }
}
