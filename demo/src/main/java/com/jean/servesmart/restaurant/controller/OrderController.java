package com.jean.servesmart.restaurant.controller;

import com.jean.servesmart.restaurant.dto.order.OrderCreateDto;
import com.jean.servesmart.restaurant.dto.order.OrderItemCreateDto;
import com.jean.servesmart.restaurant.dto.order.OrderResponseDto;
import com.jean.servesmart.restaurant.dto.order.OrderStatusUpdateDto;
import com.jean.servesmart.restaurant.exception.order.OrderInvalidDataException;
import com.jean.servesmart.restaurant.exception.order.OrderMenuItemNotFoundException;
import com.jean.servesmart.restaurant.exception.order.OrderNotFoundException;
import com.jean.servesmart.restaurant.exception.order.OrderRestaurantTableNotFoundException;
import com.jean.servesmart.restaurant.exception.order.OrderStatusNotFoundException;
import com.jean.servesmart.restaurant.exception.order.OrderUserNotFoundException;
import com.jean.servesmart.restaurant.response.ApiResponse;
import com.jean.servesmart.restaurant.service.interfaces.OrderService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final String ORDER_NOT_FOUND = "Order not found";
    private static final String ORDER_STATUS_NOT_FOUND = "Order status not found";
    private static final String INVALID_ORDER_DATA = "Invalid order data";

    private final OrderService orders;

    public OrderController(OrderService orders) {
        this.orders = orders;
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDto>> create(@Valid @RequestBody OrderCreateDto dto) {
        try {
            OrderResponseDto order = orders.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(order, "Order created successfully"));
        } catch (OrderInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(INVALID_ORDER_DATA));
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
                    .body(ApiResponse.error(ORDER_STATUS_NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create order"));
        }
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDto>> getById(@PathVariable Integer id) {
        try {
            Optional<OrderResponseDto> order = orders.getById(id);
            if (order.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(ORDER_NOT_FOUND));
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

    @RolesAllowed({"ADMIN", "STAFF"})
    @GetMapping("/table/{tableId}")
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getByTable(@PathVariable Integer tableId) {
        try {
            List<OrderResponseDto> list = orders.getByTable(tableId);
            String message = list.isEmpty() ? "No orders found for table" : "Orders retrieved successfully";
            return ResponseEntity.ok(ApiResponse.success(list, message));
        } catch (OrderInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid table id"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to load orders"));
        }
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getByStatus(@PathVariable String status) {
        try {
            List<OrderResponseDto> list = orders.getByStatus(status);
            String message = list.isEmpty() ? "No orders found for status" : "Orders retrieved successfully";
            return ResponseEntity.ok(ApiResponse.success(list, message));
        } catch (OrderInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid status"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to load orders"));
        }
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponseDto>> updateStatus(@PathVariable Integer id, @Valid @RequestBody OrderStatusUpdateDto dto) {
        try {
            OrderResponseDto updated = orders.updateStatus(id, dto);
            return ResponseEntity.ok(ApiResponse.success(updated, "Order status updated successfully"));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(ORDER_NOT_FOUND));
        } catch (OrderInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid order status update data"));
        } catch (OrderStatusNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ORDER_STATUS_NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update order status"));
        }
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        try {
            orders.delete(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Order deleted successfully"));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(ORDER_NOT_FOUND));
        } catch (OrderInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid order id"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete order"));
        }
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @PostMapping("/{orderId}/items")
    public ResponseEntity<ApiResponse<OrderResponseDto>> addItem(@PathVariable Integer orderId, @Valid @RequestBody OrderItemCreateDto dto) {
        try {
            OrderResponseDto updated = orders.addItem(orderId, dto);
            return ResponseEntity.ok(ApiResponse.success(updated, "Item added successfully"));
        } catch (OrderInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Invalid order item data"));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ORDER_NOT_FOUND));
        } catch (OrderMenuItemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Menu item not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Failed to add item"));
        }
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @PutMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<ApiResponse<OrderResponseDto>> updateItem(
            @PathVariable Integer orderId,
            @PathVariable Integer itemId,
            @Valid @RequestBody com.jean.servesmart.restaurant.dto.order.OrderItemUpdateDto dto
    ) {
        try {
            OrderResponseDto updated = orders.updateItem(orderId, itemId, dto);
            return ResponseEntity.ok(ApiResponse.success(updated, "Item updated successfully"));
        } catch (OrderInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Invalid update data"));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ORDER_NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Failed to update item"));
        }
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<ApiResponse<OrderResponseDto>> removeItem(@PathVariable Integer orderId, @PathVariable Integer itemId) {
        try {
            OrderResponseDto updated = orders.removeItem(orderId, itemId);
            return ResponseEntity.ok(ApiResponse.success(updated, "Item removed successfully"));
        } catch (OrderInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Invalid ids"));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ORDER_NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Failed to remove item"));
        }
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @GetMapping("/paid")
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getPaidOrders() {
        try {
            return ResponseEntity.ok(ApiResponse.success(orders.getPaid(), "Paid orders retrieved"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Failed to load paid orders"));
        }
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @GetMapping("/open-by-table")
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getOpenOrdersByTable() {
        try {
            return ResponseEntity.ok(ApiResponse.success(orders.getOpenByTable(), "Open orders retrieved"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Failed to load open orders"));
        }
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<OrderResponseDto>> start(@RequestBody StartOrderRequest dto) {
        try {
            OrderResponseDto order = orders.start(dto.getUserId(), dto.getRestaurantTableId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(order, "Order started"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to start order"));
        }
    }

    public static class StartOrderRequest {
        private Integer userId;
        private Integer restaurantTableId;

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getRestaurantTableId() {
            return restaurantTableId;
        }

        public void setRestaurantTableId(Integer restaurantTableId) {
            this.restaurantTableId = restaurantTableId;
        }
    }

    @RolesAllowed({"ADMIN", "STAFF"})
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<ApiResponse<OrderResponseDto>> pay(
            @PathVariable Integer orderId,
            @Valid @RequestBody com.jean.servesmart.restaurant.dto.order.PayOrderDto dto
    ) {
        try {
            OrderResponseDto updated = orders.pay(orderId, dto);
            return ResponseEntity.ok(ApiResponse.success(updated, "Order paid successfully"));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ORDER_NOT_FOUND));
        } catch (OrderInvalidDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Invalid payment data"));
        } catch (OrderStatusNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ORDER_STATUS_NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Failed to pay order"));
        }
    }
}
