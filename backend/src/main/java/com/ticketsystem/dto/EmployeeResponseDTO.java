package com.ticketsystem.dto;

import com.ticketsystem.entity.Employee.EmployeeStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmployeeResponseDTO {

    private Long id;
    private String employeeCode;
    private String employeeName;
    private String email;
    private String designation;
    private String department;
    private String assignedProject;
    private Long shiftId;
    private String shiftName;
    private EmployeeStatus status;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
