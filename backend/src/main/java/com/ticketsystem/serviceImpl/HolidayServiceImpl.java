package com.ticketsystem.serviceImpl;

import com.ticketsystem.dto.HolidayRequestDTO;
import com.ticketsystem.dto.HolidayResponseDTO;
import com.ticketsystem.entity.Holiday;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.repository.HolidayRepository;
import com.ticketsystem.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository repo;

    @Override @Transactional
    public HolidayResponseDTO createHoliday(HolidayRequestDTO req) {
        if (repo.existsByHolidayDate(req.getHolidayDate()))
            throw new IllegalArgumentException("Holiday already exists on: " + req.getHolidayDate());
        return map(repo.save(Holiday.builder().holidayName(req.getHolidayName())
                .holidayDate(req.getHolidayDate()).holidayType(req.getHolidayType()).build()));
    }

    @Override @Transactional(readOnly = true)
    public Page<HolidayResponseDTO> getAllHolidays(Pageable pageable) { return repo.findAll(pageable).map(this::map); }

    @Override @Transactional(readOnly = true)
    public HolidayResponseDTO getHolidayById(Long id) {
        return map(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Holiday not found: " + id)));
    }

    @Override @Transactional
    public HolidayResponseDTO updateHoliday(Long id, HolidayRequestDTO req) {
        Holiday h = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Holiday not found: " + id));
        if (repo.existsByHolidayDateAndIdNot(req.getHolidayDate(), id))
            throw new IllegalArgumentException("Holiday already exists on: " + req.getHolidayDate());
        h.setHolidayName(req.getHolidayName()); h.setHolidayDate(req.getHolidayDate()); h.setHolidayType(req.getHolidayType());
        return map(repo.save(h));
    }

    @Override @Transactional
    public void deleteHoliday(Long id) {
        repo.delete(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Holiday not found: " + id)));
    }

    @Override @Transactional(readOnly = true)
    public List<HolidayResponseDTO> getHolidaysByYear(int year) {
        return repo.findByYear(year).stream().map(this::map).collect(Collectors.toList());
    }

    private HolidayResponseDTO map(Holiday h) {
        return HolidayResponseDTO.builder().id(h.getId()).holidayName(h.getHolidayName())
                .holidayDate(h.getHolidayDate()).holidayType(h.getHolidayType())
                .createdAt(h.getCreatedAt()).updatedAt(h.getUpdatedAt()).build();
    }
}
