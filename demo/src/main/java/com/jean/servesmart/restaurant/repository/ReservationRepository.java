package com.jean.servesmart.restaurant.repository;

import com.jean.servesmart.restaurant.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
  
    List<Reservation> findByRestaurantTable_IdAndEventDateTimeBetween(Integer tableId, LocalDateTime start, LocalDateTime end);
    boolean existsByRestaurantTable_IdAndEventDateTime(Integer tableId, LocalDateTime eventDateTime);
    List<Reservation> findByStatus(String Status);
}
