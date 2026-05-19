package com.ticketsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectResponseDTO {
    private Long id;
    private String projectName;
    private String projectDescription;
    private String projectStatus;
    private String projectOwner;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int ticketCount;
}
