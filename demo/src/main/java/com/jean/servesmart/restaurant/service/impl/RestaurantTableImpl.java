package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.restauranttable.RestaurantTableCreateDto;
import com.jean.servesmart.restaurant.dto.restauranttable.RestaurantTableResponseDto;
import com.jean.servesmart.restaurant.dto.restauranttable.RestaurantTableUpdateDto;
import com.jean.servesmart.restaurant.exception.restauranttable.RestaurantTableInvalidDataException;
import com.jean.servesmart.restaurant.exception.restauranttable.RestaurantTableLabelAlreadyExistsException;
import com.jean.servesmart.restaurant.exception.restauranttable.RestaurantTableNotFoundException;
import com.jean.servesmart.restaurant.exception.restauranttable.RestaurantTableStatusNotFoundException;
import com.jean.servesmart.restaurant.model.RestaurantTable;
import com.jean.servesmart.restaurant.model.RestaurantTableStatus;
import com.jean.servesmart.restaurant.repository.RestaurantTableRepository;
import com.jean.servesmart.restaurant.repository.RestaurantTableStatusRepository;
import com.jean.servesmart.restaurant.service.interfaces.RestaurantTableService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RestaurantTableImpl implements RestaurantTableService {

    private final RestaurantTableRepository repo;
    private final RestaurantTableStatusRepository statusRepo;

    public RestaurantTableImpl(RestaurantTableRepository repo,
                               RestaurantTableStatusRepository statusRepo) {
        this.repo = repo;
        this.statusRepo = statusRepo;
    }

    @Override
    public RestaurantTableResponseDto create(RestaurantTableCreateDto dto) {

        if (dto == null) {
            throw new RestaurantTableInvalidDataException();
        }

        if (dto.getLabel() == null || dto.getLabel().isBlank()) {
            throw new RestaurantTableInvalidDataException("Label is required");
        }

        if (dto.getSeats() == null || dto.getSeats() <= 0) {
            throw new RestaurantTableInvalidDataException("Seats must be positive");
        }

        String label = dto.getLabel().trim();

        if (repo.findByLabel(label).isPresent()) {
            throw new RestaurantTableLabelAlreadyExistsException();
        }

        if (dto.getStatusName() == null || dto.getStatusName().isBlank()) {
            throw new RestaurantTableInvalidDataException("Status is required");
        }

        RestaurantTableStatus status = statusRepo.findByName(dto.getStatusName().trim())
                .orElseThrow(RestaurantTableStatusNotFoundException::new);

        RestaurantTable t = new RestaurantTable();
        t.setLabel(label);
        t.setSeats(dto.getSeats());
        t.setActive(dto.isActive());
        t.setStatus(status);

        RestaurantTable saved = repo.save(t);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RestaurantTableResponseDto> getById(Integer id) {
        if (id == null) {
            throw new RestaurantTableInvalidDataException();
        }

        return repo.findById(id)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantTableResponseDto> getAll() {
        return repo.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantTableResponseDto> getActive() {
        return repo.findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantTableResponseDto> getByStatus(String statusName) {
        if (statusName == null || statusName.isBlank()) {
            throw new RestaurantTableInvalidDataException("Status is required");
        }

        return repo.findByStatus_Name(statusName.trim())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RestaurantTableResponseDto update(Integer id, RestaurantTableUpdateDto dto) {
        if (id == null || dto == null) {
            throw new RestaurantTableInvalidDataException();
        }

        RestaurantTable t = repo.findById(id)
                .orElseThrow(RestaurantTableNotFoundException::new);

        if (dto.getLabel() != null) {
            if (dto.getLabel().isBlank()) {
                throw new RestaurantTableInvalidDataException("Label cannot be blank");
            }
            String newLabel = dto.getLabel().trim();

            if (!newLabel.equals(t.getLabel())
                    && repo.findByLabel(newLabel).isPresent()) {
                throw new RestaurantTableLabelAlreadyExistsException();
            }

            t.setLabel(newLabel);
        }

        if (dto.getSeats() != null) {
            if (dto.getSeats() <= 0) {
                throw new RestaurantTableInvalidDataException("Seats must be positive");
            }
            t.setSeats(dto.getSeats());
        }

        if (dto.getStatusName() != null) {
            if (dto.getStatusName().isBlank()) {
                throw new RestaurantTableInvalidDataException("Status cannot be blank");
            }

            RestaurantTableStatus status = statusRepo.findByName(dto.getStatusName().trim())
                    .orElseThrow(RestaurantTableStatusNotFoundException::new);

            t.setStatus(status);
        }

        if (dto.getActive() != null) {
            t.setActive(dto.getActive());
        }

        RestaurantTable updated = repo.save(t);
        return toResponse(updated);
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null) {
            throw new RestaurantTableInvalidDataException();
        }

        if (!repo.existsById(id)) {
            throw new RestaurantTableNotFoundException();
        }

        repo.deleteById(id);
        return true;
    }

    private RestaurantTableResponseDto toResponse(RestaurantTable t) {
        RestaurantTableResponseDto dto = new RestaurantTableResponseDto();
        dto.setId(t.getId());
        dto.setLabel(t.getLabel());
        dto.setSeats(t.getSeats());
        dto.setActive(t.isActive());
        dto.setStatusName(
                t.getStatus() != null ? t.getStatus().getName() : null);
        return dto;
    }
}
