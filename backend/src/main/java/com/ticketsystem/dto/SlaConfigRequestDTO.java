package com.ticketsystem.dto;

import com.ticketsystem.entity.Ticket.Priority;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SlaConfigRequestDTO {
    @NotNull(message = "Priority is required")
    private Priority priority;
    @NotNull @Min(1) private Integer responseTimeHours;
    @NotNull @Min(1) private Integer resolutionTimeHours;
    private Boolean excludeWeekends = true;
    private Boolean excludeHolidays = true;
}
