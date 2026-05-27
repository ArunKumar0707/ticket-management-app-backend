package com.ticketsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "holidays")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Holiday {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "holiday_name", nullable = false, length = 150)
    private String holidayName;

    @Column(name = "holiday_date", nullable = false, unique = true)
    private LocalDate holidayDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "holiday_type", nullable = false, length = 20)
    @Builder.Default
    private HolidayType holidayType = HolidayType.NATIONAL;

    @CreationTimestamp @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum HolidayType { NATIONAL, REGIONAL, OPTIONAL }
}
