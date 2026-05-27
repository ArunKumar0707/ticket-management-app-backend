package com.ticketsystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmployeeProjectMappingRequestDTO {
    @NotNull private Long employeeId;
    private Long projectId;
    private List<Long> projectIds;
    private LocalDate assignedFrom;
    private LocalDate assignedTo;
}
