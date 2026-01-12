package com.jean.servesmart.restaurant.controller;

import com.jean.servesmart.restaurant.dto.reservation.ReservationCreateDto;
import com.jean.servesmart.restaurant.dto.reservation.ReservationResponseDto;
import com.jean.servesmart.restaurant.dto.reservation.ReservationUpdateDto;
import com.jean.servesmart.restaurant.exception.reservation.ReservationInvalidDataException;
import com.jean.servesmart.restaurant.exception.reservation.ReservationNotFoundException;
import com.jean.servesmart.restaurant.exception.reservation.ReservationStatusNotFoundException;
import com.jean.servesmart.restaurant.exception.reservation.ReservationTableNotFoundException;
import com.jean.servesmart.restaurant.exception.reservation.ReservationTimeSlotUnavailableException;
import com.jean.servesmart.restaurant.response.ApiResponse;
import com.jean.servesmart.restaurant.service.interfaces.ReservationService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservations;

    public ReservationController(ReservationService reservations) {
        this.reservations = reservations;
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody ReservationCreateDto dto) {
        try {
            ReservationResponseDto reservation = reservations.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(reservation, "Reservation created successfully"));
        } catch (ReservationInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid reservation data"));
        } catch (ReservationTableNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Restaurant table not found"));
        } catch (ReservationStatusNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Reservation status not found"));
        } catch (ReservationTimeSlotUnavailableException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Time slot is unavailable"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create reservation"));
        }
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Integer id) {
        try {
            Optional<ReservationResponseDto> reservation = reservations.getById(id);
            if (reservation.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Reservation not found"));
            }
            return ResponseEntity.ok(ApiResponse.success(reservation.get(), "Reservation retrieved successfully"));
        } catch (ReservationInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid reservation id"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to load reservation"));
        }
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<?>> getByStatus(@PathVariable String status) {
        try {
            List<ReservationResponseDto> list = reservations.getByStatus(status);
            String message = list.isEmpty() ? "No reservations found for status" : "Reservations retrieved successfully";
            return ResponseEntity.ok(ApiResponse.success(list, message));
        } catch (ReservationInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid status"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to load reservations"));
        }
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @GetMapping("/table/{tableId}")
    public ResponseEntity<ApiResponse<?>> getByTableAndDateRange(
            @PathVariable Integer tableId,
            @RequestParam("start") LocalDateTime start,
            @RequestParam("end") LocalDateTime end
    ) {
        try {
            List<ReservationResponseDto> list = reservations.getByTableAndDateRange(tableId, start, end);
            String message = list.isEmpty() ? "No reservations found for given table and period" : "Reservations retrieved successfully";
            return ResponseEntity.ok(ApiResponse.success(list, message));
        } catch (ReservationInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid reservation filter data"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to load reservations"));
        }
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Integer id, @Valid @RequestBody ReservationUpdateDto dto) {
        try {
            ReservationResponseDto updated = reservations.update(id, dto);
            return ResponseEntity.ok(ApiResponse.success(updated, "Reservation updated successfully"));
        } catch (ReservationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Reservation not found"));
        } catch (ReservationInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid reservation data"));
        } catch (ReservationTableNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Restaurant table not found"));
        } catch (ReservationStatusNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Reservation status not found"));
        } catch (ReservationTimeSlotUnavailableException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Time slot is unavailable"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update reservation"));
        }
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Integer id) {
        try {
            reservations.delete(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Reservation deleted successfully"));
        } catch (ReservationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Reservation not found"));
        } catch (ReservationInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid reservation id"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete reservation"));
        }
    }
}
