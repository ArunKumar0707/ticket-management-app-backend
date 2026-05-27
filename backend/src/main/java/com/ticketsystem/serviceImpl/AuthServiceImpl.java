package com.ticketsystem.serviceImpl;

import com.ticketsystem.dto.*;
import com.ticketsystem.entity.Employee;
import com.ticketsystem.entity.Role;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.repository.EmployeeRepository;
import com.ticketsystem.security.JwtService;
import com.ticketsystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder    passwordEncoder;
    private final JwtService         jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (employeeRepository.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email already registered: " + req.getEmail());
        if (employeeRepository.existsByUsername(req.getUsername()))
            throw new IllegalArgumentException("Username already taken: " + req.getUsername());

        Employee emp = Employee.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole() != null ? req.getRole() : Role.EMPLOYEE)
                .employeeName(req.getUsername())   // sensible default
                .status(Employee.EmployeeStatus.ACTIVE)
                .isActive(true)
                .build();

        employeeRepository.save(emp);
        String token = jwtService.generateToken(emp);
        return AuthResponse.builder().token(token).role(emp.getRole().name())
                .username(emp.getUsername()).build();
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

        Employee emp = employeeRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtService.generateToken(emp);
        return AuthResponse.builder().token(token).role(emp.getRole().name())
                .username(emp.getUsername()).build();
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequest req) {
        Employee emp = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!passwordEncoder.matches(req.getCurrentPassword(), emp.getPassword()))
            throw new BadCredentialsException("Current password is incorrect");
        emp.setPassword(passwordEncoder.encode(req.getNewPassword()));
        employeeRepository.save(emp);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest req) {
        Employee emp = employeeRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Email not found: " + req.getEmail()));
        emp.setPassword(passwordEncoder.encode(req.getNewPassword()));
        employeeRepository.save(emp);
    }

    @Override
    public UserProfileResponse getUserProfile(String username) {
        Employee emp = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserProfileResponse.builder()
                .id(emp.getId())
                .username(emp.getUsername())
                .email(emp.getEmail())
                .role(emp.getRole().name())
                .employeeName(emp.getEmployeeName())
                .build();
    }
}
