package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.reservation.ReservationCreateDto;
import com.jean.servesmart.restaurant.dto.reservation.ReservationResponseDto;
import com.jean.servesmart.restaurant.dto.reservation.ReservationUpdateDto;
import com.jean.servesmart.restaurant.exception.reservation.ReservationInvalidDataException;
import com.jean.servesmart.restaurant.exception.reservation.ReservationNotFoundException;
import com.jean.servesmart.restaurant.exception.reservation.ReservationStatusNotFoundException;
import com.jean.servesmart.restaurant.exception.reservation.ReservationTableNotFoundException;
import com.jean.servesmart.restaurant.exception.reservation.ReservationTimeSlotUnavailableException;
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
import java.util.stream.Collectors;

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

        if (dto == null) {
            throw new ReservationInvalidDataException();
        }

        if (dto.getRestaurantTableId() == null) {
            throw new ReservationInvalidDataException("Table id is required");
        }

        if (dto.getFullName() == null || dto.getFullName().isBlank()) {
            throw new ReservationInvalidDataException("Full name is required");
        }

        if (dto.getPartySize() == null || dto.getPartySize() <= 0) {
            throw new ReservationInvalidDataException("Party size must be positive");
        }

        if (dto.getEventDateTime() == null) {
            throw new ReservationInvalidDataException("Event date/time is required");
        }

        RestaurantTable table = tableRepo.findById(dto.getRestaurantTableId())
                .orElseThrow(ReservationTableNotFoundException::new);

        LocalDateTime eventDateTime = dto.getEventDateTime();

        if (reservationRepo.existsByRestaurantTable_IdAndEventDateTime(table.getId(), eventDateTime)) {
            throw new ReservationTimeSlotUnavailableException();
        }

        String statusName = dto.getStatusName();
        if (statusName == null || statusName.isBlank()) {
            statusName = DEFAULT_STATUS;
        }

        ReservationStatus status = statusRepo.findByName(statusName)
                .orElseThrow(ReservationStatusNotFoundException::new);

        Reservation r = new Reservation();
        r.setRestaurantTable(table);
        r.setFullName(dto.getFullName().trim());
        r.setPartySize(dto.getPartySize());
        r.setPhoneNumber(dto.getPhoneNumber());
        r.setEventDateTime(eventDateTime);
        r.setStatus(status);

        Reservation saved = reservationRepo.save(r);
        return toResponse(saved);
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
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getByTableAndDateRange(Integer tableId, LocalDateTime start, LocalDateTime end) {
        if (tableId == null || start == null || end == null) {
            throw new ReservationInvalidDataException();
        }

        return reservationRepo.findByRestaurantTable_IdAndEventDateTimeBetween(tableId, start, end)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationResponseDto update(Integer id, ReservationUpdateDto dto) {
        if (id == null || dto == null) {
            throw new ReservationInvalidDataException();
        }

        Reservation r = reservationRepo.findById(id)
                .orElseThrow(ReservationNotFoundException::new);

        if (dto.getRestaurantTableId() != null) {
            RestaurantTable table = tableRepo.findById(dto.getRestaurantTableId())
                    .orElseThrow(ReservationTableNotFoundException::new);

            r.setRestaurantTable(table);
        }

        if (dto.getFullName() != null) {
            if (dto.getFullName().isBlank()) {
                throw new ReservationInvalidDataException("Full name cannot be blank");
            }
            r.setFullName(dto.getFullName().trim());
        }

        if (dto.getPartySize() != null) {
            if (dto.getPartySize() <= 0) {
                throw new ReservationInvalidDataException("Party size must be positive");
            }
            r.setPartySize(dto.getPartySize());
        }

        if (dto.getPhoneNumber() != null) {
            r.setPhoneNumber(dto.getPhoneNumber());
        }

        if (dto.getEventDateTime() != null) {
            LocalDateTime newEvent = dto.getEventDateTime();
            // check dubbel geboekt
            if (reservationRepo.existsByRestaurantTable_IdAndEventDateTime(
                    r.getRestaurantTable().getId(), newEvent)) {
                throw new ReservationTimeSlotUnavailableException();
            }
            r.setEventDateTime(newEvent);
        }

        if (dto.getStatusName() != null) {
            if (dto.getStatusName().isBlank()) {
                throw new ReservationInvalidDataException("Status name cannot be blank");
            }

            ReservationStatus status = statusRepo.findByName(dto.getStatusName().trim())
                    .orElseThrow(ReservationStatusNotFoundException::new);

            r.setStatus(status);
        }

        Reservation updated = reservationRepo.save(r);
        return toResponse(updated);
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null) {
            throw new ReservationInvalidDataException();
        }

        if (!reservationRepo.existsById(id)) {
            throw new ReservationNotFoundException();
        }

        reservationRepo.deleteById(id);
        return true;
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
