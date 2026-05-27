package com.ticketsystem.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmployeeProjectMappingResponseDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long projectId;
    private String projectName;
    private String projectCode;
    private LocalDate assignedFrom;
    private LocalDate assignedTo;
    private boolean isActive;
    private LocalDateTime createdAt;
}
