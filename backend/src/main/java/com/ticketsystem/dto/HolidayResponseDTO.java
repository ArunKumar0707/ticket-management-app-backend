package com.ticketsystem.dto;

import com.ticketsystem.entity.Holiday.HolidayType;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HolidayResponseDTO {
    private Long id;
    private String holidayName;
    private LocalDate holidayDate;
    private HolidayType holidayType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
