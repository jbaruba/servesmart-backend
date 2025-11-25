package com.jean.servesmart.restaurant.repository;

import com.jean.servesmart.restaurant.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationStatusRepository extends JpaRepository<ReservationStatus, Integer> {
    Optional<ReservationStatus> findByName(String name);
}
