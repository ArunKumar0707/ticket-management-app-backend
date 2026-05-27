package com.ticketsystem.dto;

import com.ticketsystem.entity.Ticket.Priority;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SlaConfigResponseDTO {
    private Long id;
    private Priority priority;
    private Integer responseTimeHours;
    private Integer resolutionTimeHours;
    private boolean excludeWeekends;
    private boolean excludeHolidays;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
