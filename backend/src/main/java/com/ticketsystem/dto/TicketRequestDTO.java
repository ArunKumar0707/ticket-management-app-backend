package com.ticketsystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TicketRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private String status = "OPEN";

    private String priority = "MEDIUM";

    private Long projectId;

    private Long assigneeId;
}
