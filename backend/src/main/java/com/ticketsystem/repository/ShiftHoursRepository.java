package com.ticketsystem.repository;

import com.ticketsystem.entity.ShiftHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ShiftHoursRepository extends JpaRepository<ShiftHours, Long> {
    boolean existsByShiftName(String shiftName);
    boolean existsByShiftNameAndIdNot(String shiftName, Long id);
    List<ShiftHours> findByIsActiveTrue();
}
