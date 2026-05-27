package com.ticketsystem.service;

import com.ticketsystem.dto.HolidayRequestDTO;
import com.ticketsystem.dto.HolidayResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface HolidayService {
    HolidayResponseDTO createHoliday(HolidayRequestDTO request);
    Page<HolidayResponseDTO> getAllHolidays(Pageable pageable);
    HolidayResponseDTO getHolidayById(Long id);
    HolidayResponseDTO updateHoliday(Long id, HolidayRequestDTO request);
    void deleteHoliday(Long id);
    List<HolidayResponseDTO> getHolidaysByYear(int year);
}
