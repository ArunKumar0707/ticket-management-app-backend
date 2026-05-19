package com.ticketsystem.dto;

import com.ticketsystem.entity.Employee.EmployeeStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequestDTO {

    @NotBlank(message = "Employee name is required")
    private String employeeName;

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    private String email;

    private String designation;

    private String department;

    private String assignedProject;

    @NotNull(message = "Status is required")
    private EmployeeStatus status;
}
