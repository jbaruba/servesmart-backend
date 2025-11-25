package com.jean.servesmart.restaurant.service.interfaces;

import com.jean.servesmart.restaurant.dto.Reservation.ReservationCreateDto;
import com.jean.servesmart.restaurant.dto.Reservation.ReservationResponseDto;
import com.jean.servesmart.restaurant.dto.Reservation.ReservationUpdateDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationService {

    ReservationResponseDto create(ReservationCreateDto dto);

    ReservationResponseDto update(Integer id, ReservationUpdateDto dto);

    boolean delete(Integer id);

    Optional<ReservationResponseDto> getById(Integer id);

    List<ReservationResponseDto> getByStatus(String statusName);

    List<ReservationResponseDto> getByTableAndDateRange(Integer tableId, LocalDateTime start, LocalDateTime end);
}
