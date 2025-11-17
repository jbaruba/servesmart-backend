package com.jean.servesmart.restaurant.repository;

import com.jean.servesmart.restaurant.model.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoginLogRepository extends JpaRepository<LoginLog, Integer> {
    List<LoginLog> findByUser_IdOrderByDateDesc(Integer userId);
}
