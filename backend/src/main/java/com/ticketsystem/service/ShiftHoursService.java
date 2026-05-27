package com.ticketsystem.service;

import com.ticketsystem.dto.ShiftHoursRequestDTO;
import com.ticketsystem.dto.ShiftHoursResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ShiftHoursService {
    ShiftHoursResponseDTO createShift(ShiftHoursRequestDTO request);
    Page<ShiftHoursResponseDTO> getAllShifts(Pageable pageable);
    ShiftHoursResponseDTO getShiftById(Long id);
    ShiftHoursResponseDTO updateShift(Long id, ShiftHoursRequestDTO request);
    void deleteShift(Long id);
    List<ShiftHoursResponseDTO> getActiveShifts();
}
