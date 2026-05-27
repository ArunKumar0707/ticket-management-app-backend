package com.ticketsystem.serviceImpl;

import com.ticketsystem.dto.*;
import com.ticketsystem.entity.Employee;
import com.ticketsystem.entity.Role;
import com.ticketsystem.entity.User;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.repository.EmployeeRepository;
import com.ticketsystem.repository.UserRepository;
import com.ticketsystem.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ApiResponse<UserListResponse> createUser(RegisterRequest request) {
        log.info("Admin creating user: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }
        if (request.getEmployeeId() != null
                && userRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new IllegalArgumentException("Employee already has a linked user account.");
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

        // Update Employee.userId to maintain bidirectional reference
        if (saved.getEmployeeId() != null) {
            employeeRepository.findById(saved.getEmployeeId()).ifPresent(emp -> {
                emp.setUserId(saved.getId());
                employeeRepository.save(emp);
            });
        }

        log.info("User created: id={}", saved.getId());
        return ApiResponse.success("User created successfully", mapToListResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserListResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToListResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserListResponse> searchUsers(String search, Role role, Boolean isActive, Pageable pageable) {
        return userRepository.searchUsers(search, role, isActive, pageable).map(this::mapToListResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserListResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToListResponse(user);
    }

    @Override
    @Transactional
    public ApiResponse<UserListResponse> updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (userRepository.existsByUsernameAndIdNot(request.getUsername(), id)) {
            throw new IllegalArgumentException("Username already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }
        if (request.getEmployeeId() != null
                && userRepository.existsByEmployeeIdAndIdNot(request.getEmployeeId(), id)) {
            throw new IllegalArgumentException("Employee already has a linked user account.");
        }

        // Clear old employee link if changed
        Long oldEmployeeId = user.getEmployeeId();
        if (oldEmployeeId != null && !oldEmployeeId.equals(request.getEmployeeId())) {
            employeeRepository.findById(oldEmployeeId).ifPresent(emp -> {
                emp.setUserId(null);
                employeeRepository.save(emp);
            });
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setEmployeeId(request.getEmployeeId());
        if (request.getIsActive() != null) {
            user.setActive(request.getIsActive());
        }

        User updated = userRepository.save(user);

        // Update new employee link
        if (updated.getEmployeeId() != null) {
            employeeRepository.findById(updated.getEmployeeId()).ifPresent(emp -> {
                emp.setUserId(updated.getId());
                employeeRepository.save(emp);
            });
        }

        return ApiResponse.success("User updated successfully", mapToListResponse(updated));
    }

    @Override
    @Transactional
    public ApiResponse<Void> activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setActive(true);
        userRepository.save(user);
        log.info("User {} activated", user.getUsername());
        return ApiResponse.success("User activated successfully", null);
    }

    @Override
    @Transactional
    public ApiResponse<Void> deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setActive(false);
        userRepository.save(user);
        log.info("User {} deactivated", user.getUsername());
        return ApiResponse.success("User deactivated successfully", null);
    }

    @Override
    @Transactional
    public ApiResponse<Void> resetPassword(Long id, ResetPasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password reset for user: {}", user.getUsername());
        return ApiResponse.success("Password reset successfully", null);
    }

    @Override
    @Transactional
    public ApiResponse<Void> changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for user: {}", username);
        return ApiResponse.success("Password changed successfully", null);
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private UserListResponse mapToListResponse(User user) {
        String employeeName = null;
        if (user.getEmployeeId() != null) {
            employeeName = employeeRepository.findById(user.getEmployeeId())
                    .map(Employee::getEmployeeName)
                    .orElse(null);
        }
        return UserListResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .employeeId(user.getEmployeeId())
                .employeeName(employeeName)
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
