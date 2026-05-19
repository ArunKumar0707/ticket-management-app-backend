package com.ticketsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long projectId;
    private String projectName;
    private Long assigneeId;
    private String assigneeName;
}
