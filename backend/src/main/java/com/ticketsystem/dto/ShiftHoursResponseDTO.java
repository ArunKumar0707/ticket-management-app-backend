package com.ticketsystem.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ShiftHoursResponseDTO {
    private Long id;
    private String shiftName;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal workingHours;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
