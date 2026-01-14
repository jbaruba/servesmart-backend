package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.order.*;
import com.jean.servesmart.restaurant.exception.order.*;
import com.jean.servesmart.restaurant.model.*;
import com.jean.servesmart.restaurant.repository.*;
import com.jean.servesmart.restaurant.service.interfaces.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderImpl implements OrderService {

        private final OrdersRepository ordersRepo;
        private final OrderItemRepository orderItemRepo;
        private final OrdersStatusRepository statusRepo;
        private final MenuItemsRepository menuItemsRepo;
        private final RestaurantTableRepository tableRepo;
        private final RestaurantTableStatusRepository tableStatusRepo;
        private final UserRepository userRepo;

        private static final String DEFAULT_STATUS = "NEW";

        public OrderImpl(
                        OrdersRepository ordersRepo,
                        OrderItemRepository orderItemRepo,
                        OrdersStatusRepository statusRepo,
                        MenuItemsRepository menuItemsRepo,
                        RestaurantTableRepository tableRepo,
                        RestaurantTableStatusRepository tableStatusRepo,
                        UserRepository userRepo) {
                this.ordersRepo = ordersRepo;
                this.orderItemRepo = orderItemRepo;
                this.statusRepo = statusRepo;
                this.menuItemsRepo = menuItemsRepo;
                this.tableRepo = tableRepo;
                this.tableStatusRepo = tableStatusRepo;
                this.userRepo = userRepo;
        }

        @Override
        public OrderResponseDto create(OrderCreateDto dto) {
                if (dto == null || dto.getUserId() == null)
                        throw new OrderInvalidDataException("User id is required");

                if (dto.getItems() == null || dto.getItems().isEmpty())
                        throw new OrderInvalidDataException("At least one item is required");

                User user = userRepo.findById(dto.getUserId())
                                .orElseThrow(OrderUserNotFoundException::new);

                RestaurantTable table = null;
                if (dto.getRestaurantTableId() != null) {
                        table = tableRepo.findById(dto.getRestaurantTableId())
                                        .orElseThrow(OrderRestaurantTableNotFoundException::new);
                }

                OrdersStatus status = statusRepo.findByName(DEFAULT_STATUS)
                                .orElseThrow(OrderStatusNotFoundException::new);

                Orders order = new Orders();
                order.setUser(user);
                order.setRestaurantTable(table);
                order.setStatus(status);

                Orders savedOrder = ordersRepo.save(order);

                List<OrderItem> items = new ArrayList<>();
                for (OrderItemCreateDto itemDto : dto.getItems()) {
                        MenuItems menuItem = menuItemsRepo.findById(itemDto.getMenuItemId())
                                        .orElseThrow(OrderMenuItemNotFoundException::new);

                        OrderItem item = new OrderItem();
                        item.setOrder(savedOrder);
                        item.setMenuItem(menuItem);
                        item.setItemsName(menuItem.getName());
                        item.setItemsPrice(menuItem.getPrice());
                        item.setItemsQuantity(itemDto.getQuantity());
                        item.setNotes(itemDto.getNotes());
                        item.setActive(true);

                        items.add(orderItemRepo.save(item));
                }

                savedOrder.setOrderItems(items);
                return toResponse(savedOrder);
        }

        @Override
        public Optional<OrderResponseDto> getById(Integer id) {
                if (id == null)
                        throw new OrderInvalidDataException();
                return ordersRepo.findById(id).map(this::toResponse);
        }

        @Override
        public List<OrderResponseDto> getByTable(Integer tableId) {
                return ordersRepo.findByRestaurantTable_Id(tableId)
                                .stream()
                                .map(this::toResponse)
                                .toList();
        }

        @Override
        public List<OrderResponseDto> getByStatus(String statusName) {
                return ordersRepo.findByStatus_Name(statusName)
                                .stream()
                                .map(this::toResponse)
                                .toList();
        }

        @Override
        public OrderResponseDto addItem(Integer orderId, OrderItemCreateDto dto) {
                Orders order = ordersRepo.findById(orderId)
                                .orElseThrow(OrderNotFoundException::new);

                MenuItems menuItem = menuItemsRepo.findById(dto.getMenuItemId())
                                .orElseThrow(OrderMenuItemNotFoundException::new);

                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setMenuItem(menuItem);
                item.setItemsName(menuItem.getName());
                item.setItemsPrice(menuItem.getPrice());
                item.setItemsQuantity(dto.getQuantity());
                item.setNotes(dto.getNotes());
                item.setActive(true);

                orderItemRepo.save(item);
                order.setOrderItems(orderItemRepo.findByOrder_Id(orderId));

                return toResponse(order);
        }

        @Override
        public OrderResponseDto updateItem(Integer orderId, Integer itemId, OrderItemUpdateDto dto) {
                OrderItem item = orderItemRepo.findByIdAndOrder_Id(itemId, orderId)
                                .orElseThrow(OrderInvalidDataException::new);

                if (dto.getQuantity() != null)
                        item.setItemsQuantity(dto.getQuantity());
                if (dto.getNotes() != null)
                        item.setNotes(dto.getNotes());
                if (dto.getActive() != null)
                        item.setActive(dto.getActive());

                orderItemRepo.save(item);

                Orders order = ordersRepo.findById(orderId)
                                .orElseThrow(OrderNotFoundException::new);
                order.setOrderItems(orderItemRepo.findByOrder_Id(orderId));

                return toResponse(order);
        }

        @Override
        public OrderResponseDto removeItem(Integer orderId, Integer itemId) {
                OrderItem item = orderItemRepo.findByIdAndOrder_Id(itemId, orderId)
                                .orElseThrow(OrderInvalidDataException::new);

                orderItemRepo.delete(item);

                Orders order = ordersRepo.findById(orderId)
                                .orElseThrow(OrderNotFoundException::new);
                order.setOrderItems(orderItemRepo.findByOrder_Id(orderId));

                return toResponse(order);
        }

        @Override
        public OrderResponseDto start(Integer userId, Integer restaurantTableId) {
                User user = userRepo.findById(userId)
                                .orElseThrow(OrderUserNotFoundException::new);

                RestaurantTable table = tableRepo.findById(restaurantTableId)
                                .orElseThrow(OrderRestaurantTableNotFoundException::new);

                RestaurantTableStatus occupied = tableStatusRepo.findByName("OCCUPIED").orElseThrow();

                table.setStatus(occupied);
                tableRepo.save(table);

                OrdersStatus status = statusRepo.findByName("NEW")
                                .orElseThrow(OrderStatusNotFoundException::new);

                Orders order = new Orders();
                order.setUser(user);
                order.setRestaurantTable(table);
                order.setStatus(status);

                Orders saved = ordersRepo.save(order);
                saved.setOrderItems(new ArrayList<>());

                return toResponse(saved);
        }

        @Override
        public OrderResponseDto pay(Integer orderId, PayOrderDto dto) {
                Orders order = ordersRepo.findById(orderId)
                                .orElseThrow(OrderNotFoundException::new);

                OrdersStatus paid = statusRepo.findByName("PAID")
                                .orElseThrow(OrderStatusNotFoundException::new);
                order.setStatus(paid);

                RestaurantTable table = order.getRestaurantTable();
                if (table != null) {
                        RestaurantTableStatus available = tableStatusRepo.findByName("AVAILABLE").orElseThrow();
                        table.setStatus(available);
                        tableRepo.save(table);
                }

                order.setOrderItems(orderItemRepo.findByOrder_Id(orderId));
                return toResponse(ordersRepo.save(order));
        }

        @Override
        public List<OrderResponseDto> getPaid() {
                return ordersRepo.findByStatus_Name("PAID")
                                .stream()
                                .map(this::toResponse)
                                .toList();
        }

        @Override
        public List<OrderResponseDto> getOpenByTable() {
                return ordersRepo.findByStatus_NameNotIn(List.of("PAID", "CANCELLED"))
                                .stream()
                                .map(this::toResponse)
                                .toList();
        }

        private OrderResponseDto toResponse(Orders o) {
                OrderResponseDto dto = new OrderResponseDto();
                dto.setId(o.getId());
                dto.setUserId(o.getUser() != null ? o.getUser().getId() : null);
                dto.setUserEmail(o.getUser() != null ? o.getUser().getEmail() : null);
                dto.setRestaurantTableId(o.getRestaurantTable() != null ? o.getRestaurantTable().getId() : null);
                dto.setRestaurantTableLabel(o.getRestaurantTable() != null ? o.getRestaurantTable().getLabel() : null);
                dto.setStatusName(o.getStatus() != null ? o.getStatus().getName() : null);
                dto.setCreatedAt(o.getCreateAt());

                if (o.getOrderItems() != null) {
                        dto.setItems(o.getOrderItems().stream()
                                        .map(this::toItemResponse)
                                        .toList());
                }
                return dto;
        }

        private OrderItemResponseDto toItemResponse(OrderItem i) {
                OrderItemResponseDto dto = new OrderItemResponseDto();
                dto.setId(i.getId());
                dto.setMenuItemId(i.getMenuItem().getId());
                dto.setMenuItemName(i.getMenuItem().getName());
                dto.setItemsName(i.getItemsName());
                dto.setItemsPrice(i.getItemsPrice());
                dto.setItemsQuantity(i.getItemsQuantity());
                dto.setNotes(i.getNotes());
                dto.setActive(i.isActive());
                return dto;
        }

        @Override
        public boolean delete(Integer id) {
                if (id == null)
                        throw new OrderInvalidDataException();
                if (!ordersRepo.existsById(id))
                        throw new OrderNotFoundException();

                orderItemRepo.deleteByOrder_Id(id);
                ordersRepo.deleteById(id);
                return true;
        }

        @Override
        public OrderResponseDto updateStatus(Integer id, OrderStatusUpdateDto dto) {
                if (id == null || dto == null || dto.getStatusName() == null)
                        throw new OrderInvalidDataException();

                Orders order = ordersRepo.findById(id)
                                .orElseThrow(OrderNotFoundException::new);

                OrdersStatus status = statusRepo.findByName(dto.getStatusName())
                                .orElseThrow(OrderStatusNotFoundException::new);

                order.setStatus(status);
                return toResponse(ordersRepo.save(order));
        }
}
