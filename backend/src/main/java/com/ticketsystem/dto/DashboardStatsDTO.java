package com.ticketsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalTickets;
    private long openTickets;
    private long inProgressTickets;
    private long resolvedTickets;
    private long closedTickets;
    private long highPriorityTickets;
    private long mediumPriorityTickets;
    private long lowPriorityTickets;
    private long totalProjects;
    private long activeProjects;
    private long totalEmployees;
    private long activeEmployees;
}
