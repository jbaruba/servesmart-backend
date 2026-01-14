package com.jean.servesmart.restaurant.controller;

import com.jean.servesmart.restaurant.dto.restauranttable.RestaurantTableCreateDto;
import com.jean.servesmart.restaurant.dto.restauranttable.RestaurantTableResponseDto;
import com.jean.servesmart.restaurant.dto.restauranttable.RestaurantTableUpdateDto;
import com.jean.servesmart.restaurant.exception.restauranttable.RestaurantTableInvalidDataException;
import com.jean.servesmart.restaurant.exception.restauranttable.RestaurantTableLabelAlreadyExistsException;
import com.jean.servesmart.restaurant.exception.restauranttable.RestaurantTableNotFoundException;
import com.jean.servesmart.restaurant.exception.restauranttable.RestaurantTableStatusNotFoundException;
import com.jean.servesmart.restaurant.response.ApiResponse;
import com.jean.servesmart.restaurant.service.interfaces.RestaurantTableService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/restaurant-tables")
public class RestaurantTableController {

    private static final String RESTAURANT_TABLE_NOT_FOUND = "Restaurant table not found";
    private static final String FAILED_TO_LOAD_TABLES = "Failed to load restaurant tables";

    private final RestaurantTableService tables;

    public RestaurantTableController(RestaurantTableService tables) {
        this.tables = tables;
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @PostMapping
    public ResponseEntity<ApiResponse<RestaurantTableResponseDto>> create(@Valid @RequestBody RestaurantTableCreateDto dto) {
        try {
            RestaurantTableResponseDto table = tables.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(table, "Restaurant table created successfully"));
        } catch (RestaurantTableInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid table data"));
        } catch (RestaurantTableLabelAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Table label already exists"));
        } catch (RestaurantTableStatusNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Table status not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create restaurant table"));
        }
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @GetMapping
    public ResponseEntity<ApiResponse<List<RestaurantTableResponseDto>>> getAll() {
        try {
            List<RestaurantTableResponseDto> list = tables.getAll();
            String message = list.isEmpty() ? "No restaurant tables found" : "Restaurant tables retrieved successfully";
            return ResponseEntity.ok(ApiResponse.success(list, message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(FAILED_TO_LOAD_TABLES));
        }
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<RestaurantTableResponseDto>>> getActive() {
        try {
            List<RestaurantTableResponseDto> list = tables.getActive();
            String message = list.isEmpty() ? "No active restaurant tables found" : "Active restaurant tables retrieved successfully";
            return ResponseEntity.ok(ApiResponse.success(list, message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(FAILED_TO_LOAD_TABLES));
        }
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<RestaurantTableResponseDto>>> getByStatus(@PathVariable String status) {
        try {
            List<RestaurantTableResponseDto> list = tables.getByStatus(status);
            String message = list.isEmpty() ? "No tables found for status" : "Restaurant tables retrieved successfully";
            return ResponseEntity.ok(ApiResponse.success(list, message));
        } catch (RestaurantTableInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid status"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(FAILED_TO_LOAD_TABLES));
        }
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantTableResponseDto>> getById(@PathVariable Integer id) {
        try {
            Optional<RestaurantTableResponseDto> table = tables.getById(id);
            if (table.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(RESTAURANT_TABLE_NOT_FOUND));
            }
            return ResponseEntity.ok(ApiResponse.success(table.get(), "Restaurant table retrieved successfully"));
        } catch (RestaurantTableInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid table id"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to load restaurant table"));
        }
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantTableResponseDto>> update(@PathVariable Integer id, @Valid @RequestBody RestaurantTableUpdateDto dto) {
        try {
            RestaurantTableResponseDto updated = tables.update(id, dto);
            return ResponseEntity.ok(ApiResponse.success(updated, "Restaurant table updated successfully"));
        } catch (RestaurantTableNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(RESTAURANT_TABLE_NOT_FOUND));
        } catch (RestaurantTableInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid table data"));
        } catch (RestaurantTableLabelAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Table label already exists"));
        } catch (RestaurantTableStatusNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Table status not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update restaurant table"));
        }
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        try {
            tables.delete(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Restaurant table deleted successfully"));
        } catch (RestaurantTableNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(RESTAURANT_TABLE_NOT_FOUND));
        } catch (RestaurantTableInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid table id"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete restaurant table"));
        }
    }
}
