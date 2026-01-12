package com.jean.servesmart.restaurant.service.interfaces;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.jean.servesmart.restaurant.dto.reservation.ReservationCreateDto;
import com.jean.servesmart.restaurant.dto.reservation.ReservationResponseDto;
import com.jean.servesmart.restaurant.dto.reservation.ReservationUpdateDto;

public interface ReservationService {

    ReservationResponseDto create(ReservationCreateDto dto);

    ReservationResponseDto update(Integer id, ReservationUpdateDto dto);

    boolean delete(Integer id);

    Optional<ReservationResponseDto> getById(Integer id);

    List<ReservationResponseDto> getByStatus(String statusName);

    List<ReservationResponseDto> getByTableAndDateRange(Integer tableId, LocalDateTime start, LocalDateTime end);
}
