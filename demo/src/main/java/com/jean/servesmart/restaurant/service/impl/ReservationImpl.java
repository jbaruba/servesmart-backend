package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.reservation.ReservationCreateDto;
import com.jean.servesmart.restaurant.dto.reservation.ReservationResponseDto;
import com.jean.servesmart.restaurant.dto.reservation.ReservationUpdateDto;
import com.jean.servesmart.restaurant.exception.reservation.*;
import com.jean.servesmart.restaurant.model.Reservation;
import com.jean.servesmart.restaurant.model.ReservationStatus;
import com.jean.servesmart.restaurant.model.RestaurantTable;
import com.jean.servesmart.restaurant.repository.ReservationRepository;
import com.jean.servesmart.restaurant.repository.ReservationStatusRepository;
import com.jean.servesmart.restaurant.repository.RestaurantTableRepository;
import com.jean.servesmart.restaurant.service.interfaces.ReservationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservationImpl implements ReservationService {

    private final ReservationRepository reservationRepo;
    private final RestaurantTableRepository tableRepo;
    private final ReservationStatusRepository statusRepo;

    private static final String DEFAULT_STATUS = "PENDING";

    public ReservationImpl(
            ReservationRepository reservationRepo,
            RestaurantTableRepository tableRepo,
            ReservationStatusRepository statusRepo) {
        this.reservationRepo = reservationRepo;
        this.tableRepo = tableRepo;
        this.statusRepo = statusRepo;
    }

    @Override
    public ReservationResponseDto create(ReservationCreateDto dto) {
        validateCreate(dto);

        RestaurantTable table = tableRepo.findById(dto.getRestaurantTableId())
                .orElseThrow(ReservationTableNotFoundException::new);

        if (reservationRepo.existsByRestaurantTable_IdAndEventDateTime(
                table.getId(), dto.getEventDateTime())) {
            throw new ReservationTimeSlotUnavailableException();
        }

        ReservationStatus status = resolveStatus(dto.getStatusName());

        Reservation r = new Reservation();
        r.setRestaurantTable(table);
        r.setFullName(dto.getFullName().trim());
        r.setPartySize(dto.getPartySize());
        r.setPhoneNumber(dto.getPhoneNumber());
        r.setEventDateTime(dto.getEventDateTime());
        r.setStatus(status);

        return toResponse(reservationRepo.save(r));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReservationResponseDto> getById(Integer id) {
        if (id == null) {
            throw new ReservationInvalidDataException();
        }
        return reservationRepo.findById(id).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getByStatus(String statusName) {
        if (statusName == null || statusName.isBlank()) {
            throw new ReservationInvalidDataException("Status name is required");
        }

        return reservationRepo.findByStatus_Name(statusName.trim())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getByTableAndDateRange(
            Integer tableId, LocalDateTime start, LocalDateTime end) {

        if (tableId == null || start == null || end == null) {
            throw new ReservationInvalidDataException();
        }

        return reservationRepo
                .findByRestaurantTable_IdAndEventDateTimeBetween(tableId, start, end)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ReservationResponseDto update(Integer id, ReservationUpdateDto dto) {
        validateUpdate(id, dto);

        Reservation r = reservationRepo.findById(id)
                .orElseThrow(ReservationNotFoundException::new);

        updateTable(r, dto);
        updateName(r, dto);
        updatePartySize(r, dto);
        updatePhone(r, dto);
        updateEventTime(r, dto);
        updateStatus(r, dto);

        return toResponse(reservationRepo.save(r));
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null || !reservationRepo.existsById(id)) {
            throw new ReservationNotFoundException();
        }
        reservationRepo.deleteById(id);
        return true;
    }

    private void validateCreate(ReservationCreateDto dto) {
        if (dto == null
                || dto.getRestaurantTableId() == null
                || dto.getFullName() == null
                || dto.getFullName().isBlank()
                || dto.getPartySize() == null
                || dto.getPartySize() <= 0
                || dto.getEventDateTime() == null) {
            throw new ReservationInvalidDataException();
        }
    }

    private void validateUpdate(Integer id, ReservationUpdateDto dto) {
        if (id == null || dto == null) {
            throw new ReservationInvalidDataException();
        }
    }

    private ReservationStatus resolveStatus(String statusName) {
        String name = statusName == null || statusName.isBlank()
                ? DEFAULT_STATUS
                : statusName.trim();

        return statusRepo.findByName(name)
                .orElseThrow(ReservationStatusNotFoundException::new);
    }

    private void updateTable(Reservation r, ReservationUpdateDto dto) {
        if (dto.getRestaurantTableId() == null) return;

        RestaurantTable table = tableRepo.findById(dto.getRestaurantTableId())
                .orElseThrow(ReservationTableNotFoundException::new);
        r.setRestaurantTable(table);
    }

    private void updateName(Reservation r, ReservationUpdateDto dto) {
        if (dto.getFullName() == null) return;
        if (dto.getFullName().isBlank()) {
            throw new ReservationInvalidDataException("Full name cannot be blank");
        }
        r.setFullName(dto.getFullName().trim());
    }

    private void updatePartySize(Reservation r, ReservationUpdateDto dto) {
        if (dto.getPartySize() == null) return;
        if (dto.getPartySize() <= 0) {
            throw new ReservationInvalidDataException("Party size must be positive");
        }
        r.setPartySize(dto.getPartySize());
    }

    private void updatePhone(Reservation r, ReservationUpdateDto dto) {
        if (dto.getPhoneNumber() != null) {
            r.setPhoneNumber(dto.getPhoneNumber());
        }
    }

    private void updateEventTime(Reservation r, ReservationUpdateDto dto) {
        if (dto.getEventDateTime() == null) return;

        if (reservationRepo.existsByRestaurantTable_IdAndEventDateTime(
                r.getRestaurantTable().getId(), dto.getEventDateTime())) {
            throw new ReservationTimeSlotUnavailableException();
        }

        r.setEventDateTime(dto.getEventDateTime());
    }

    private void updateStatus(Reservation r, ReservationUpdateDto dto) {
        if (dto.getStatusName() == null) return;
        if (dto.getStatusName().isBlank()) {
            throw new ReservationInvalidDataException("Status name cannot be blank");
        }

        ReservationStatus status = statusRepo.findByName(dto.getStatusName().trim())
                .orElseThrow(ReservationStatusNotFoundException::new);

        r.setStatus(status);
    }

    private ReservationResponseDto toResponse(Reservation r) {
        ReservationResponseDto dto = new ReservationResponseDto();
        dto.setId(r.getId());
        dto.setRestaurantTableId(
                r.getRestaurantTable() != null ? r.getRestaurantTable().getId() : null);
        dto.setRestaurantTableLabel(
                r.getRestaurantTable() != null ? r.getRestaurantTable().getLabel() : null);
        dto.setFullName(r.getFullName());
        dto.setPartySize(r.getPartySize());
        dto.setPhoneNumber(r.getPhoneNumber());
        dto.setEventDateTime(r.getEventDateTime());
        dto.setStatusName(
                r.getStatus() != null ? r.getStatus().getName() : null);
        return dto;
    }
}
