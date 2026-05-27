package com.ticketsystem.serviceImpl;

import com.ticketsystem.dto.*;
import com.ticketsystem.entity.User;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public ApiResponse<AuthResponse> register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .employeeId(request.getEmployeeId())
                .isActive(true)
                .build();

        User saved = userRepository.save(user);
        log.info("User registered with id: {}", saved.getId());

        String token = jwtService.generateToken(saved);

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(saved.getId())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .role(saved.getRole())
                .employeeId(saved.getEmployeeId())
                .build();

        return ApiResponse.success("User registered successfully", authResponse);
    }

    @Override
    public ApiResponse<AuthResponse> login(LoginRequest request) {
        log.info("Login attempt for: {}", request.getUsernameOrEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid username/email or password");
        }

        User user = userRepository
                .findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new IllegalArgumentException("Account is deactivated. Please contact administrator.");
        }

        String token = jwtService.generateToken(user);

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .employeeId(user.getEmployeeId())
                .build();

        log.info("User logged in: {}", user.getUsername());
        return ApiResponse.success("Login successful", authResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<UserProfileResponse> getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        UserProfileResponse profile = UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .employeeId(user.getEmployeeId())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();

        return ApiResponse.success("Profile retrieved successfully", profile);
    }
}
