package com.jean.servesmart.restaurant.service.interfaces;

import java.util.List;
import java.util.Optional;

import com.jean.servesmart.restaurant.dto.restauranttable.RestaurantTableCreateDto;
import com.jean.servesmart.restaurant.dto.restauranttable.RestaurantTableResponseDto;
import com.jean.servesmart.restaurant.dto.restauranttable.RestaurantTableUpdateDto;

public interface RestaurantTableService {

    RestaurantTableResponseDto create(RestaurantTableCreateDto dto);

    RestaurantTableResponseDto update(Integer id, RestaurantTableUpdateDto dto);

    boolean delete(Integer id);

    Optional<RestaurantTableResponseDto> getById(Integer id);

    List<RestaurantTableResponseDto> getAll();

    List<RestaurantTableResponseDto> getActive();

    List<RestaurantTableResponseDto> getByStatus(String statusName);
}
