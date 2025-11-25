package com.jean.servesmart.restaurant.controller;

import com.jean.servesmart.restaurant.dto.Order.OrderCreateDto;
import com.jean.servesmart.restaurant.dto.Order.OrderResponseDto;
import com.jean.servesmart.restaurant.dto.Order.OrderStatusUpdateDto;
import com.jean.servesmart.restaurant.exception.order.OrderInvalidDataException;
import com.jean.servesmart.restaurant.exception.order.OrderMenuItemNotFoundException;
import com.jean.servesmart.restaurant.exception.order.OrderNotFoundException;
import com.jean.servesmart.restaurant.exception.order.OrderRestaurantTableNotFoundException;
import com.jean.servesmart.restaurant.exception.order.OrderStatusNotFoundException;
import com.jean.servesmart.restaurant.exception.order.OrderUserNotFoundException;
import com.jean.servesmart.restaurant.response.ApiResponse;
import com.jean.servesmart.restaurant.service.interfaces.OrderService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orders;

    public OrderController(OrderService orders) {
        this.orders = orders;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody OrderCreateDto dto) {
        try {
            OrderResponseDto order = orders.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(order, "Order created successfully"));

        } catch (OrderInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid order data"));

        } catch (OrderUserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("User not found"));

        } catch (OrderRestaurantTableNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Restaurant table not found"));

        } catch (OrderMenuItemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("One or more menu items not found"));

        } catch (OrderStatusNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Order status not found"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create order"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Integer id) {
        try {
            Optional<OrderResponseDto> order = orders.getById(id);
            if (order.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Order not found"));
            }
            return ResponseEntity.ok(ApiResponse.success(order.get(), "Order retrieved successfully"));

        } catch (OrderInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid order id"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to load order"));
        }
    }

    @GetMapping("/table/{tableId}")
    public ResponseEntity<ApiResponse<?>> getByTable(@PathVariable Integer tableId) {
        try {
            List<OrderResponseDto> list = orders.getByTable(tableId);

            String message = list.isEmpty()
                    ? "No orders found for table"
                    : "Orders retrieved successfully";

            return ResponseEntity.ok(ApiResponse.success(list, message));

        } catch (OrderInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid table id"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to load orders"));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<?>> getByStatus(@PathVariable String status) {
        try {
            List<OrderResponseDto> list = orders.getByStatus(status);

            String message = list.isEmpty()
                    ? "No orders found for status"
                    : "Orders retrieved successfully";

            return ResponseEntity.ok(ApiResponse.success(list, message));

        } catch (OrderInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid status"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to load orders"));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<?>> updateStatus(@PathVariable Integer id,
                                                       @Valid @RequestBody OrderStatusUpdateDto dto) {
        try {
            OrderResponseDto updated = orders.updateStatus(id, dto);
            return ResponseEntity.ok(ApiResponse.success(updated, "Order status updated successfully"));

        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Order not found"));

        } catch (OrderInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid order status update data"));

        } catch (OrderStatusNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Order status not found"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update order status"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Integer id) {
        try {
            orders.delete(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Order deleted successfully"));

        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Order not found"));

        } catch (OrderInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid order id"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete order"));
        }
    }
}
