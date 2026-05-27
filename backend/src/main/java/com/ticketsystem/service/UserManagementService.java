package com.ticketsystem.service;

import com.ticketsystem.dto.*;
import com.ticketsystem.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserManagementService {

    ApiResponse<UserListResponse> createUser(RegisterRequest request);

    Page<UserListResponse> getAllUsers(Pageable pageable);

    Page<UserListResponse> searchUsers(String search, Role role, Boolean isActive, Pageable pageable);

    UserListResponse getUserById(Long id);

    ApiResponse<UserListResponse> updateUser(Long id, UpdateUserRequest request);

    ApiResponse<Void> activateUser(Long id);

    ApiResponse<Void> deactivateUser(Long id);

    ApiResponse<Void> resetPassword(Long id, ResetPasswordRequest request);

    ApiResponse<Void> changePassword(String username, ChangePasswordRequest request);
}
