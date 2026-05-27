package com.ticketsystem.entity;

import com.ticketsystem.entity.Ticket.Priority;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "sla_config")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SlaConfig {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, unique = true, length = 20)
    private Priority priority;

    @Column(name = "response_time_hours", nullable = false)
    private Integer responseTimeHours;

    @Column(name = "resolution_time_hours", nullable = false)
    private Integer resolutionTimeHours;

    @Column(name = "exclude_weekends", nullable = false)
    @Builder.Default
    private boolean excludeWeekends = true;

    @Column(name = "exclude_holidays", nullable = false)
    @Builder.Default
    private boolean excludeHolidays = true;

    @CreationTimestamp @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
