package com.ticketsystem.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ShiftHoursRequestDTO {
    @NotBlank(message = "Shift name is required")
    private String shiftName;
    @NotNull(message = "Start time is required")
    private LocalTime startTime;
    @NotNull(message = "End time is required")
    private LocalTime endTime;
    @NotNull @DecimalMin("0.5")
    private BigDecimal workingHours;
    private Boolean isActive = true;
}
