package com.ticketsystem.dto;

import com.ticketsystem.entity.Holiday.HolidayType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HolidayRequestDTO {
    @NotBlank(message = "Holiday name is required")
    private String holidayName;
    @NotNull(message = "Holiday date is required")
    private LocalDate holidayDate;
    @NotNull(message = "Holiday type is required")
    private HolidayType holidayType;
}
