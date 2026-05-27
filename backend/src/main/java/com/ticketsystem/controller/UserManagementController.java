package com.ticketsystem.controller;

import com.ticketsystem.dto.*;
import com.ticketsystem.entity.Employee;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.repository.EmployeeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Kept for backward-compatibility with the frontend /api/users endpoints.
 * All data now lives in the employees table (merged entity).
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder    passwordEncoder;

    /** Change own password */
    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest req,
            Principal principal) {
        Employee emp = employeeRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!passwordEncoder.matches(req.getCurrentPassword(), emp.getPassword()))
            throw new IllegalArgumentException("Current password is incorrect");
        emp.setPassword(passwordEncoder.encode(req.getNewPassword()));
        employeeRepository.save(emp);
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed"));
    }

    /** Activate employee account */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable Long id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
        emp.setActive(true);
        employeeRepository.save(emp);
        return ResponseEntity.ok(ApiResponse.success(null, "Activated"));
    }

    /** Deactivate employee account */
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
        emp.setActive(false);
        employeeRepository.save(emp);
        return ResponseEntity.ok(ApiResponse.success(null, "Deactivated"));
    }
}
