package com.ticketsystem.service;

import com.ticketsystem.dto.EmployeeRequestDTO;
import com.ticketsystem.dto.EmployeeResponseDTO;

import java.util.List;

public interface EmployeeService {
    List<EmployeeResponseDTO> getAllEmployees();
    EmployeeResponseDTO getEmployeeById(Long id);
    EmployeeResponseDTO createEmployee(EmployeeRequestDTO dto);
    EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO dto);
    void deleteEmployee(Long id);
    List<EmployeeResponseDTO> searchEmployees(String query);
}
