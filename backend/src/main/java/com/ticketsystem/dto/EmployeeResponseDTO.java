package com.ticketsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmployeeResponseDTO {
    private Long id;
    private String employeeName;
    private String email;
    private String phoneNumber;
    private String department;
    private String role;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int assignedTicketCount;
}
