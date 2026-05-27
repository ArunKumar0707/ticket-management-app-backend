package com.ticketsystem.serviceImpl;

import com.ticketsystem.dto.ShiftHoursRequestDTO;
import com.ticketsystem.dto.ShiftHoursResponseDTO;
import com.ticketsystem.entity.ShiftHours;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.repository.ShiftHoursRepository;
import com.ticketsystem.service.ShiftHoursService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Slf4j
public class ShiftHoursServiceImpl implements ShiftHoursService {

    private final ShiftHoursRepository repo;

    @Override @Transactional
    public ShiftHoursResponseDTO createShift(ShiftHoursRequestDTO req) {
        if (repo.existsByShiftName(req.getShiftName()))
            throw new IllegalArgumentException("Shift name already exists: " + req.getShiftName());
        return map(repo.save(ShiftHours.builder()
                .shiftName(req.getShiftName()).startTime(req.getStartTime())
                .endTime(req.getEndTime()).workingHours(req.getWorkingHours())
                .isActive(req.getIsActive() != null ? req.getIsActive() : true).build()));
    }

    @Override @Transactional(readOnly = true)
    public Page<ShiftHoursResponseDTO> getAllShifts(Pageable pageable) {
        return repo.findAll(pageable).map(this::map);
    }

    @Override @Transactional(readOnly = true)
    public ShiftHoursResponseDTO getShiftById(Long id) {
        return map(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + id)));
    }

    @Override @Transactional
    public ShiftHoursResponseDTO updateShift(Long id, ShiftHoursRequestDTO req) {
        ShiftHours s = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + id));
        if (repo.existsByShiftNameAndIdNot(req.getShiftName(), id))
            throw new IllegalArgumentException("Shift name already exists: " + req.getShiftName());
        s.setShiftName(req.getShiftName()); s.setStartTime(req.getStartTime());
        s.setEndTime(req.getEndTime()); s.setWorkingHours(req.getWorkingHours());
        if (req.getIsActive() != null) s.setActive(req.getIsActive());
        return map(repo.save(s));
    }

    @Override @Transactional
    public void deleteShift(Long id) {
        ShiftHours s = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + id));
        s.setActive(false); repo.save(s);
    }

    @Override @Transactional(readOnly = true)
    public List<ShiftHoursResponseDTO> getActiveShifts() {
        return repo.findByIsActiveTrue().stream().map(this::map).collect(Collectors.toList());
    }

    private ShiftHoursResponseDTO map(ShiftHours s) {
        return ShiftHoursResponseDTO.builder()
                .id(s.getId()).shiftName(s.getShiftName()).startTime(s.getStartTime())
                .endTime(s.getEndTime()).workingHours(s.getWorkingHours())
                .isActive(s.isActive()).createdAt(s.getCreatedAt()).updatedAt(s.getUpdatedAt()).build();
    }
}
