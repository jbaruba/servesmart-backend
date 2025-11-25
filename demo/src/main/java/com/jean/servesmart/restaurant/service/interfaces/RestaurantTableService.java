package com.jean.servesmart.restaurant.service.interfaces;

import com.jean.servesmart.restaurant.dto.RestaurantTable.RestaurantTableCreateDto;
import com.jean.servesmart.restaurant.dto.RestaurantTable.RestaurantTableResponseDto;
import com.jean.servesmart.restaurant.dto.RestaurantTable.RestaurantTableUpdateDto;

import java.util.List;
import java.util.Optional;

public interface RestaurantTableService {

    RestaurantTableResponseDto create(RestaurantTableCreateDto dto);

    RestaurantTableResponseDto update(Integer id, RestaurantTableUpdateDto dto);

    boolean delete(Integer id);

    Optional<RestaurantTableResponseDto> getById(Integer id);

    List<RestaurantTableResponseDto> getAll();

    List<RestaurantTableResponseDto> getActive();

    List<RestaurantTableResponseDto> getByStatus(String statusName);
}
