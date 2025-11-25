package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.Order.OrderCreateDto;
import com.jean.servesmart.restaurant.dto.Order.OrderItemCreateDto;
import com.jean.servesmart.restaurant.dto.Order.OrderItemResponseDto;
import com.jean.servesmart.restaurant.dto.Order.OrderResponseDto;
import com.jean.servesmart.restaurant.dto.Order.OrderStatusUpdateDto;
import com.jean.servesmart.restaurant.exception.order.OrderInvalidDataException;
import com.jean.servesmart.restaurant.exception.order.OrderMenuItemNotFoundException;
import com.jean.servesmart.restaurant.exception.order.OrderNotFoundException;
import com.jean.servesmart.restaurant.exception.order.OrderRestaurantTableNotFoundException;
import com.jean.servesmart.restaurant.exception.order.OrderStatusNotFoundException;
import com.jean.servesmart.restaurant.exception.order.OrderUserNotFoundException;
import com.jean.servesmart.restaurant.model.MenuItems;
import com.jean.servesmart.restaurant.model.OrderItem;
import com.jean.servesmart.restaurant.model.Orders;
import com.jean.servesmart.restaurant.model.OrdersStatus;
import com.jean.servesmart.restaurant.model.RestaurantTable;
import com.jean.servesmart.restaurant.model.User;
import com.jean.servesmart.restaurant.repository.MenuItemsRepository;
import com.jean.servesmart.restaurant.repository.OrderItemRepository;
import com.jean.servesmart.restaurant.repository.OrdersRepository;
import com.jean.servesmart.restaurant.repository.OrdersStatusRepository;
import com.jean.servesmart.restaurant.repository.RestaurantTableRepository;
import com.jean.servesmart.restaurant.repository.UserRepository;
import com.jean.servesmart.restaurant.service.interfaces.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderImpl implements OrderService {

    private final OrdersRepository ordersRepo;
    private final OrderItemRepository orderItemRepo;
    private final OrdersStatusRepository statusRepo;
    private final MenuItemsRepository menuItemsRepo;
    private final RestaurantTableRepository tableRepo;
    private final UserRepository userRepo;

    private static final String DEFAULT_STATUS = "NEW";

    public OrderImpl(OrdersRepository ordersRepo,
                     OrderItemRepository orderItemRepo,
                     OrdersStatusRepository statusRepo,
                     MenuItemsRepository menuItemsRepo,
                     RestaurantTableRepository tableRepo,
                     UserRepository userRepo) {
        this.ordersRepo = ordersRepo;
        this.orderItemRepo = orderItemRepo;
        this.statusRepo = statusRepo;
        this.menuItemsRepo = menuItemsRepo;
        this.tableRepo = tableRepo;
        this.userRepo = userRepo;
    }

    @Override
    public OrderResponseDto create(OrderCreateDto dto) {

        if (dto == null) {
            throw new OrderInvalidDataException();
        }

        if (dto.getUserId() == null) {
            throw new OrderInvalidDataException("User id is required");
        }

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new OrderInvalidDataException("At least one order item is required");
        }

        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(OrderUserNotFoundException::new);

        RestaurantTable table = null;
        if (dto.getRestaurantTableId() != null) {
            table = tableRepo.findById(dto.getRestaurantTableId())
                    .orElseThrow(OrderRestaurantTableNotFoundException::new);
        }

        String statusName = dto.getStatusName();
        if (statusName == null || statusName.isBlank()) {
            statusName = DEFAULT_STATUS;
        }

        OrdersStatus status = statusRepo.findByName(statusName.trim())
                .orElseThrow(OrderStatusNotFoundException::new);

        Orders order = new Orders();
        order.setUser(user);
        order.setRestaurantTable(table);
        order.setStatus(status);

        Orders savedOrder = ordersRepo.save(order);

        List<OrderItem> savedItems = new ArrayList<>();

        for (OrderItemCreateDto itemDto : dto.getItems()) {

            if (itemDto == null || itemDto.getMenuItemId() == null) {
                throw new OrderInvalidDataException("Order item is invalid");
            }

            if (itemDto.getQuantity() == null || itemDto.getQuantity() <= 0) {
                throw new OrderInvalidDataException("Quantity must be positive");
            }

            MenuItems menuItem = menuItemsRepo.findById(itemDto.getMenuItemId())
                    .orElseThrow(OrderMenuItemNotFoundException::new);

            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setMenuItem(menuItem);
            item.setItemsName(menuItem.getName());
            item.setItemsPrice(menuItem.getPrice());
            item.setItemsQuantity(itemDto.getQuantity());
            item.setActive(true);
            item.setNotes(itemDto.getNotes());

            savedItems.add(orderItemRepo.save(item));
        }

        savedOrder.setOrderItems(savedItems);

        return toResponse(savedOrder);
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null) {
            throw new OrderInvalidDataException();
        }

        if (!ordersRepo.existsById(id)) {
            throw new OrderNotFoundException();
        }

        orderItemRepo.deleteByOrder_Id(id);
        ordersRepo.deleteById(id);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderResponseDto> getById(Integer id) {
        if (id == null) {
            throw new OrderInvalidDataException();
        }

        return ordersRepo.findById(id)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getByTable(Integer tableId) {
        if (tableId == null) {
            throw new OrderInvalidDataException();
        }

        return ordersRepo.findByRestaurantTable_Id(tableId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getByStatus(String statusName) {
        if (statusName == null || statusName.isBlank()) {
            throw new OrderInvalidDataException("Status name is required");
        }

        return ordersRepo.findByStatus_Name(statusName.trim())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDto updateStatus(Integer id, OrderStatusUpdateDto dto) {
        if (id == null || dto == null) {
            throw new OrderInvalidDataException();
        }

        if (dto.getStatusName() == null || dto.getStatusName().isBlank()) {
            throw new OrderInvalidDataException("Status name is required");
        }

        Orders order = ordersRepo.findById(id)
                .orElseThrow(OrderNotFoundException::new);

        OrdersStatus status = statusRepo.findByName(dto.getStatusName().trim())
                .orElseThrow(OrderStatusNotFoundException::new);

        order.setStatus(status);

        Orders updated = ordersRepo.save(order);
        return toResponse(updated);
    }

    private OrderResponseDto toResponse(Orders o) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(o.getId());
        dto.setUserId(o.getUser() != null ? o.getUser().getId() : null);
        dto.setUserEmail(o.getUser() != null ? o.getUser().getEmail() : null);
        dto.setRestaurantTableId(
                o.getRestaurantTable() != null ? o.getRestaurantTable().getId() : null);
        dto.setRestaurantTableLabel(
                o.getRestaurantTable() != null ? o.getRestaurantTable().getLabel() : null);
        dto.setStatusName(o.getStatus() != null ? o.getStatus().getName() : null);
        dto.setCreatedAt(o.getCreateAt());

        if (o.getOrderItems() != null) {
            dto.setItems(
                    o.getOrderItems()
                            .stream()
                            .map(this::toItemResponse)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    private OrderItemResponseDto toItemResponse(OrderItem i) {
        OrderItemResponseDto dto = new OrderItemResponseDto();
        dto.setId(i.getId());
        dto.setMenuItemId(i.getMenuItem() != null ? i.getMenuItem().getId() : null);
        dto.setMenuItemName(i.getMenuItem() != null ? i.getMenuItem().getName() : null);
        dto.setItemsName(i.getItemsName());
        dto.setItemsPrice(i.getItemsPrice());
        dto.setItemsQuantity(i.getItemsQuantity());
        dto.setNotes(i.getNotes());
        dto.setActive(i.isActive());
        return dto;
    }
}
