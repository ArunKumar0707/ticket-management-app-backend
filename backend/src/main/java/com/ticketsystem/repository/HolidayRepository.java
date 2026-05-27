package com.ticketsystem.repository;

import com.ticketsystem.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    boolean existsByHolidayDate(LocalDate holidayDate);
    boolean existsByHolidayDateAndIdNot(LocalDate holidayDate, Long id);

    @Query("SELECT h FROM Holiday h WHERE YEAR(h.holidayDate) = :year ORDER BY h.holidayDate ASC")
    List<Holiday> findByYear(@Param("year") int year);

    @Query("SELECT h.holidayDate FROM Holiday h WHERE h.holidayDate BETWEEN :from AND :to")
    List<LocalDate> findHolidayDatesBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
