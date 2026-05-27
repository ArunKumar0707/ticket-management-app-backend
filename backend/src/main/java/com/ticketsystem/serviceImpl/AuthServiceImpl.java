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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public ApiResponse<AuthResponse> register(RegisterRequest req) {

        if (employeeRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already registered: " + req.getEmail()
            );
        }

        if (employeeRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException(
                    "Username already taken: " + req.getUsername()
            );
        }

        Employee employee = Employee.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(
                        passwordEncoder.encode(req.getPassword())
                )
                .role(
                        req.getRole() != null
                                ? req.getRole()
                                : Role.EMPLOYEE
                )
                .employeeName(req.getUsername())
                .status(Employee.EmployeeStatus.ACTIVE)
                .isActive(true)
                .build();

        employeeRepository.save(employee);

        String token =
                jwtService.generateToken(employee);

        AuthResponse response =
                AuthResponse.builder()
                        .token(token)
                        .role(employee.getRole().name())
                        .username(employee.getUsername())
                        .build();

        return ApiResponse.success(
                "Registration successful",
                response
        );
    }

    @Override
    public ApiResponse<AuthResponse> login(
            LoginRequest req
    ) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getUsernameOrEmail(),
                        req.getPassword()
                )
        );

        Employee employee =
                employeeRepository
                        .findByUsername(
                                req.getUsernameOrEmail()
                        )
                        .or(() ->
                                employeeRepository.findByEmail(
                                        req.getUsernameOrEmail()
                                )
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        String token =
                jwtService.generateToken(employee);

        AuthResponse response =
                AuthResponse.builder()
                        .token(token)
                        .role(employee.getRole().name())
                        .username(employee.getUsername())
                        .build();

        return ApiResponse.success(
                "Login successful",
                response
        );
    }

    @Override
    public ApiResponse<UserProfileResponse> getProfile(
            String username
    ) {

        Employee employee =
                employeeRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        UserProfileResponse profile =
                UserProfileResponse.builder()
                        .id(employee.getId())
                        .username(employee.getUsername())
                        .email(employee.getEmail())
                        .role(employee.getRole().name())
                        .employeeName(
                                employee.getEmployeeName()
                        )
                        .build();

        return ApiResponse.success(
                "Profile retrieved successfully",
                profile
        );
    }

    @Transactional
    public void changePassword(
            String username,
            ChangePasswordRequest req
    ) {

        Employee employee =
                employeeRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        if (!passwordEncoder.matches(
                req.getCurrentPassword(),
                employee.getPassword()
        )) {
            throw new BadCredentialsException(
                    "Current password is incorrect"
            );
        }

        employee.setPassword(
                passwordEncoder.encode(
                        req.getNewPassword()
                )
        );

        employeeRepository.save(employee);
    }

    @Transactional
    public void resetPassword(
            String username,
            ResetPasswordRequest req
    ) {

        Employee employee =
                employeeRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        employee.setPassword(
                passwordEncoder.encode(
                        req.getNewPassword()
                )
        );

        employeeRepository.save(employee);
    }
}