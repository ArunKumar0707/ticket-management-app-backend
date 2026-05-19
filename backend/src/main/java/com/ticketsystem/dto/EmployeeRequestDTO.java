package com.ticketsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmployeeRequestDTO {

    @NotBlank(message = "Employee name is required")
    private String employeeName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phoneNumber;

    private String department;

    private String role;

    private String status = "ACTIVE";
}
