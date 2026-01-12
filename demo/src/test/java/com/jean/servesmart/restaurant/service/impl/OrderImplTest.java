package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.order.*;
import com.jean.servesmart.restaurant.exception.order.*;
import com.jean.servesmart.restaurant.model.*;
import com.jean.servesmart.restaurant.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderImplTest {

    @Mock
    private OrdersRepository ordersRepo;

    @Mock
    private OrderItemRepository orderItemRepo;

    @Mock
    private OrdersStatusRepository statusRepo;

    @Mock
    private MenuItemsRepository menuItemsRepo;

    @Mock
    private RestaurantTableRepository tableRepo;

    @Mock
    private RestaurantTableStatusRepository tableStatusRepo;

    @Mock
    private UserRepository userRepo;

    private OrderImpl service;

    @BeforeEach
    void setup() {
        service = new OrderImpl(ordersRepo, orderItemRepo, statusRepo, menuItemsRepo, tableRepo, tableStatusRepo, userRepo);
    }

    @Test
    void create_whenDtoNull_throwsInvalidData() {
        assertThrows(OrderInvalidDataException.class, () -> service.create(null));
        verifyNoInteractions(ordersRepo, orderItemRepo, statusRepo, menuItemsRepo, tableRepo, tableStatusRepo, userRepo);
    }

    @Test
    void create_whenUserIdNull_throwsInvalidData() {
        OrderCreateDto dto = new OrderCreateDto();
        dto.setUserId(null);
        dto.setItems(List.of(new OrderItemCreateDto()));

        assertThrows(OrderInvalidDataException.class, () -> service.create(dto));
        verifyNoInteractions(ordersRepo, orderItemRepo, statusRepo, menuItemsRepo, tableRepo, tableStatusRepo, userRepo);
    }

    @Test
    void create_whenItemsNull_throwsInvalidData() {
        OrderCreateDto dto = new OrderCreateDto();
        dto.setUserId(1);
        dto.setItems(null);

        assertThrows(OrderInvalidDataException.class, () -> service.create(dto));
        verifyNoInteractions(ordersRepo, orderItemRepo, statusRepo, menuItemsRepo, tableRepo, tableStatusRepo, userRepo);
    }

    @Test
    void create_whenItemsEmpty_throwsInvalidData() {
        OrderCreateDto dto = new OrderCreateDto();
        dto.setUserId(1);
        dto.setItems(new ArrayList<>());

        assertThrows(OrderInvalidDataException.class, () -> service.create(dto));
        verifyNoInteractions(ordersRepo, orderItemRepo, statusRepo, menuItemsRepo, tableRepo, tableStatusRepo, userRepo);
    }

    @Test
    void create_whenUserNotFound_throwsUserNotFound() {
        OrderCreateDto dto = new OrderCreateDto();
        dto.setUserId(1);
        OrderItemCreateDto itemDto = new OrderItemCreateDto();
        itemDto.setMenuItemId(10);
        itemDto.setQuantity(2);
        dto.setItems(List.of(itemDto));

        when(userRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(OrderUserNotFoundException.class, () -> service.create(dto));

        verify(userRepo).findById(1);
        verifyNoInteractions(ordersRepo, orderItemRepo, statusRepo, menuItemsRepo, tableRepo, tableStatusRepo);
    }

    @Test
    void create_whenTableIdProvidedButNotFound_throwsTableNotFound() {
        OrderCreateDto dto = new OrderCreateDto();
        dto.setUserId(1);
        dto.setRestaurantTableId(5);
        OrderItemCreateDto itemDto = new OrderItemCreateDto();
        itemDto.setMenuItemId(10);
        itemDto.setQuantity(2);
        dto.setItems(List.of(itemDto));

        when(userRepo.findById(1)).thenReturn(Optional.of(new User()));
        when(tableRepo.findById(5)).thenReturn(Optional.empty());

        assertThrows(OrderRestaurantTableNotFoundException.class, () -> service.create(dto));

        verify(userRepo).findById(1);
        verify(tableRepo).findById(5);
        verifyNoInteractions(statusRepo, ordersRepo, menuItemsRepo, orderItemRepo, tableStatusRepo);
    }

    @Test
    void create_whenDefaultStatusNotFound_throwsStatusNotFound() {
        OrderCreateDto dto = new OrderCreateDto();
        dto.setUserId(1);
        OrderItemCreateDto itemDto = new OrderItemCreateDto();
        itemDto.setMenuItemId(10);
        itemDto.setQuantity(2);
        dto.setItems(List.of(itemDto));

        when(userRepo.findById(1)).thenReturn(Optional.of(new User()));
        when(statusRepo.findByName("NEW")).thenReturn(Optional.empty());

        assertThrows(OrderStatusNotFoundException.class, () -> service.create(dto));

        verify(userRepo).findById(1);
        verify(statusRepo).findByName("NEW");
        verifyNoInteractions(ordersRepo, menuItemsRepo, orderItemRepo, tableRepo, tableStatusRepo);
    }

    @Test
    void create_whenMenuItemNotFound_throwsMenuItemNotFound() {
        OrderCreateDto dto = new OrderCreateDto();
        dto.setUserId(1);
        OrderItemCreateDto itemDto = new OrderItemCreateDto();
        itemDto.setMenuItemId(10);
        itemDto.setQuantity(2);
        itemDto.setNotes("n");
        dto.setItems(List.of(itemDto));

        User user = new User();
        user.setId(1);
        user.setEmail("u@e.com");

        OrdersStatus status = new OrdersStatus();
        status.setName("NEW");

        Orders savedOrder = new Orders();
        savedOrder.setId(100);
        savedOrder.setUser(user);
        savedOrder.setStatus(status);
        savedOrder.setRestaurantTable(null);
        savedOrder.setCreateAt(LocalDateTime.now());

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(statusRepo.findByName("NEW")).thenReturn(Optional.of(status));
        when(ordersRepo.save(any(Orders.class))).thenReturn(savedOrder);
        when(menuItemsRepo.findById(10)).thenReturn(Optional.empty());

        assertThrows(OrderMenuItemNotFoundException.class, () -> service.create(dto));

        verify(menuItemsRepo).findById(10);
    }

    @Test
    void create_whenValidWithoutTable_savesOrderAndItems_andReturnsDto() {
        OrderCreateDto dto = new OrderCreateDto();
        dto.setUserId(1);
        OrderItemCreateDto itemDto = new OrderItemCreateDto();
        itemDto.setMenuItemId(10);
        itemDto.setQuantity(2);
        itemDto.setNotes("no onions");
        dto.setItems(List.of(itemDto));

        User user = new User();
        user.setId(1);
        user.setEmail("u@e.com");

        OrdersStatus status = new OrdersStatus();
        status.setName("NEW");

        MenuItems menuItem = new MenuItems();
        menuItem.setId(10);
        menuItem.setName("Burger");
        menuItem.setPrice(BigDecimal.valueOf(12.5));

        Orders savedOrder = new Orders();
        savedOrder.setId(100);
        savedOrder.setUser(user);
        savedOrder.setRestaurantTable(null);
        savedOrder.setStatus(status);
        savedOrder.setCreateAt(LocalDateTime.now());

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(statusRepo.findByName("NEW")).thenReturn(Optional.of(status));
        when(ordersRepo.save(any(Orders.class))).thenReturn(savedOrder);
        when(menuItemsRepo.findById(10)).thenReturn(Optional.of(menuItem));

        when(orderItemRepo.save(any(OrderItem.class))).thenAnswer(inv -> {
            OrderItem oi = inv.getArgument(0);
            oi.setId(200);
            return oi;
        });

        OrderResponseDto result = service.create(dto);

        assertNotNull(result);
        assertEquals(100, result.getId());
        assertEquals(1, result.getUserId());
        assertEquals("u@e.com", result.getUserEmail());
        assertNull(result.getRestaurantTableId());
        assertNull(result.getRestaurantTableLabel());
        assertEquals("NEW", result.getStatusName());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals(200, result.getItems().get(0).getId());
        assertEquals(10, result.getItems().get(0).getMenuItemId());
        assertEquals("Burger", result.getItems().get(0).getMenuItemName());
        assertEquals("Burger", result.getItems().get(0).getItemsName());
        assertEquals(BigDecimal.valueOf(12.5), result.getItems().get(0).getItemsPrice());
        assertEquals(2, result.getItems().get(0).getItemsQuantity());
        assertEquals("no onions", result.getItems().get(0).getNotes());
        assertTrue(result.getItems().get(0).isActive());

        ArgumentCaptor<Orders> orderCaptor = ArgumentCaptor.forClass(Orders.class);
        verify(ordersRepo).save(orderCaptor.capture());
        assertEquals(user, orderCaptor.getValue().getUser());
        assertNull(orderCaptor.getValue().getRestaurantTable());
        assertEquals(status, orderCaptor.getValue().getStatus());

        ArgumentCaptor<OrderItem> itemCaptor = ArgumentCaptor.forClass(OrderItem.class);
        verify(orderItemRepo).save(itemCaptor.capture());
        assertEquals(savedOrder, itemCaptor.getValue().getOrder());
        assertEquals(menuItem, itemCaptor.getValue().getMenuItem());
        assertEquals("Burger", itemCaptor.getValue().getItemsName());
        assertEquals(BigDecimal.valueOf(12.5), itemCaptor.getValue().getItemsPrice());
        assertEquals(2, itemCaptor.getValue().getItemsQuantity());
        assertEquals("no onions", itemCaptor.getValue().getNotes());
        assertTrue(itemCaptor.getValue().isActive());
    }

    @Test
    void create_whenValidWithTable_setsTableAndReturnsDto() {
        OrderCreateDto dto = new OrderCreateDto();
        dto.setUserId(1);
        dto.setRestaurantTableId(5);
        OrderItemCreateDto itemDto = new OrderItemCreateDto();
        itemDto.setMenuItemId(10);
        itemDto.setQuantity(1);
        dto.setItems(List.of(itemDto));

        User user = new User();
        user.setId(1);
        user.setEmail("u@e.com");

        RestaurantTable table = new RestaurantTable();
        table.setId(5);
        table.setLabel("T5");

        OrdersStatus status = new OrdersStatus();
        status.setName("NEW");

        MenuItems menuItem = new MenuItems();
        menuItem.setId(10);
        menuItem.setName("Burger");
        menuItem.setPrice(BigDecimal.valueOf(12));

        Orders savedOrder = new Orders();
        savedOrder.setId(100);
        savedOrder.setUser(user);
        savedOrder.setRestaurantTable(table);
        savedOrder.setStatus(status);
        savedOrder.setCreateAt(LocalDateTime.now());

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(tableRepo.findById(5)).thenReturn(Optional.of(table));
        when(statusRepo.findByName("NEW")).thenReturn(Optional.of(status));
        when(ordersRepo.save(any(Orders.class))).thenReturn(savedOrder);
        when(menuItemsRepo.findById(10)).thenReturn(Optional.of(menuItem));
        when(orderItemRepo.save(any(OrderItem.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponseDto result = service.create(dto);

        assertEquals(5, result.getRestaurantTableId());
        assertEquals("T5", result.getRestaurantTableLabel());
        assertEquals(1, result.getItems().size());

        verify(tableRepo).findById(5);
    }

    @Test
    void getById_whenIdNull_throwsInvalidData() {
        assertThrows(OrderInvalidDataException.class, () -> service.getById(null));
        verifyNoInteractions(ordersRepo, orderItemRepo, statusRepo, menuItemsRepo, tableRepo, tableStatusRepo, userRepo);
    }

    @Test
    void getById_whenNotFound_returnsEmpty() {
        when(ordersRepo.findById(1)).thenReturn(Optional.empty());

        Optional<OrderResponseDto> result = service.getById(1);

        assertTrue(result.isEmpty());
        verify(ordersRepo).findById(1);
    }

    @Test
    void getByTable_mapsOrders() {
        OrdersStatus status = new OrdersStatus();
        status.setName("NEW");

        Orders o = new Orders();
        o.setId(1);
        o.setStatus(status);
        o.setOrderItems(new ArrayList<>());

        when(ordersRepo.findByRestaurantTable_Id(5)).thenReturn(List.of(o));

        List<OrderResponseDto> result = service.getByTable(5);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("NEW", result.get(0).getStatusName());
        verify(ordersRepo).findByRestaurantTable_Id(5);
    }

    @Test
    void getByStatus_mapsOrders() {
        OrdersStatus status = new OrdersStatus();
        status.setName("NEW");

        Orders o = new Orders();
        o.setId(1);
        o.setStatus(status);
        o.setOrderItems(new ArrayList<>());

        when(ordersRepo.findByStatus_Name("NEW")).thenReturn(List.of(o));

        List<OrderResponseDto> result = service.getByStatus("NEW");

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        verify(ordersRepo).findByStatus_Name("NEW");
    }

    @Test
    void addItem_whenOrderNotFound_throwsNotFound() {
        when(ordersRepo.findById(1)).thenReturn(Optional.empty());

        OrderItemCreateDto dto = new OrderItemCreateDto();
        dto.setMenuItemId(10);
        dto.setQuantity(1);

        assertThrows(OrderNotFoundException.class, () -> service.addItem(1, dto));

        verify(ordersRepo).findById(1);
        verifyNoInteractions(menuItemsRepo, orderItemRepo);
    }

    @Test
    void addItem_whenMenuItemNotFound_throwsMenuItemNotFound() {
        Orders order = new Orders();
        order.setId(1);

        when(ordersRepo.findById(1)).thenReturn(Optional.of(order));
        when(menuItemsRepo.findById(10)).thenReturn(Optional.empty());

        OrderItemCreateDto dto = new OrderItemCreateDto();
        dto.setMenuItemId(10);
        dto.setQuantity(1);

        assertThrows(OrderMenuItemNotFoundException.class, () -> service.addItem(1, dto));

        verify(menuItemsRepo).findById(10);
    }

    @Test
    void addItem_whenValid_savesItemAndReturnsOrderWithItems() {
        Orders order = new Orders();
        order.setId(1);

        MenuItems menuItem = new MenuItems();
        menuItem.setId(10);
        menuItem.setName("Burger");
        menuItem.setPrice(BigDecimal.valueOf(12));

        OrderItem savedItem = new OrderItem();
        savedItem.setId(200);
        savedItem.setOrder(order);
        savedItem.setMenuItem(menuItem);
        savedItem.setItemsName("Burger");
        savedItem.setItemsPrice(BigDecimal.valueOf(12));
        savedItem.setItemsQuantity(2);
        savedItem.setNotes("n");
        savedItem.setActive(true);

        when(ordersRepo.findById(1)).thenReturn(Optional.of(order));
        when(menuItemsRepo.findById(10)).thenReturn(Optional.of(menuItem));
        when(orderItemRepo.save(any(OrderItem.class))).thenReturn(savedItem);
        when(orderItemRepo.findByOrder_Id(1)).thenReturn(List.of(savedItem));

        OrderItemCreateDto dto = new OrderItemCreateDto();
        dto.setMenuItemId(10);
        dto.setQuantity(2);
        dto.setNotes("n");

        OrderResponseDto result = service.addItem(1, dto);

        assertEquals(1, result.getId());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals(200, result.getItems().get(0).getId());
        assertEquals(10, result.getItems().get(0).getMenuItemId());
        assertEquals(2, result.getItems().get(0).getItemsQuantity());

        verify(orderItemRepo).save(any(OrderItem.class));
        verify(orderItemRepo).findByOrder_Id(1);
    }

    @Test
    void updateItem_whenItemNotFoundForOrder_throwsInvalidData() {
        when(orderItemRepo.findByIdAndOrder_Id(10, 1)).thenReturn(Optional.empty());

        OrderItemUpdateDto dto = new OrderItemUpdateDto();
        dto.setQuantity(2);

        assertThrows(OrderInvalidDataException.class, () -> service.updateItem(1, 10, dto));

        verify(orderItemRepo).findByIdAndOrder_Id(10, 1);
        verifyNoMoreInteractions(orderItemRepo);
        verifyNoInteractions(ordersRepo);
    }

    @Test
    void updateItem_whenValid_updatesFieldsAndReturnsOrder() {
        Orders order = new Orders();
        order.setId(1);

        MenuItems menuItem = new MenuItems();
        menuItem.setId(10);
        menuItem.setName("Burger");
        menuItem.setPrice(BigDecimal.valueOf(12));

        OrderItem item = new OrderItem();
        item.setId(200);
        item.setOrder(order);
        item.setMenuItem(menuItem);
        item.setItemsName("Burger");
        item.setItemsPrice(BigDecimal.valueOf(12));
        item.setItemsQuantity(1);
        item.setNotes("old");
        item.setActive(true);

        when(orderItemRepo.findByIdAndOrder_Id(200, 1)).thenReturn(Optional.of(item));
        when(orderItemRepo.save(any(OrderItem.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ordersRepo.findById(1)).thenReturn(Optional.of(order));
        when(orderItemRepo.findByOrder_Id(1)).thenReturn(List.of(item));

        OrderItemUpdateDto dto = new OrderItemUpdateDto();
        dto.setQuantity(3);
        dto.setNotes("new");
        dto.setActive(false);

        OrderResponseDto result = service.updateItem(1, 200, dto);

        assertEquals(1, result.getId());
        assertEquals(1, result.getItems().size());
        assertEquals(3, result.getItems().get(0).getItemsQuantity());
        assertEquals("new", result.getItems().get(0).getNotes());
        assertFalse(result.getItems().get(0).isActive());

        verify(orderItemRepo).save(item);
        verify(orderItemRepo).findByOrder_Id(1);
        verify(ordersRepo).findById(1);
    }

    @Test
    void removeItem_whenItemNotFoundForOrder_throwsInvalidData() {
        when(orderItemRepo.findByIdAndOrder_Id(200, 1)).thenReturn(Optional.empty());

        assertThrows(OrderInvalidDataException.class, () -> service.removeItem(1, 200));

        verify(orderItemRepo).findByIdAndOrder_Id(200, 1);
        verifyNoInteractions(ordersRepo);
    }

    @Test
    void removeItem_whenValid_deletesItemAndReturnsOrder() {
        Orders order = new Orders();
        order.setId(1);

        MenuItems menuItem = new MenuItems();
        menuItem.setId(10);
        menuItem.setName("Burger");
        menuItem.setPrice(BigDecimal.valueOf(12));

        OrderItem item = new OrderItem();
        item.setId(200);
        item.setOrder(order);
        item.setMenuItem(menuItem);
        item.setItemsName("Burger");
        item.setItemsPrice(BigDecimal.valueOf(12));
        item.setItemsQuantity(1);
        item.setActive(true);

        when(orderItemRepo.findByIdAndOrder_Id(200, 1)).thenReturn(Optional.of(item));
        when(ordersRepo.findById(1)).thenReturn(Optional.of(order));
        when(orderItemRepo.findByOrder_Id(1)).thenReturn(List.of());

        OrderResponseDto result = service.removeItem(1, 200);

        assertEquals(1, result.getId());
        assertNotNull(result.getItems());
        assertEquals(0, result.getItems().size());

        verify(orderItemRepo).delete(item);
        verify(orderItemRepo).findByOrder_Id(1);
        verify(ordersRepo).findById(1);
    }

    @Test
    void start_whenUserNotFound_throwsUserNotFound() {
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(OrderUserNotFoundException.class, () -> service.start(1, 5));

        verify(userRepo).findById(1);
        verifyNoInteractions(tableRepo, tableStatusRepo, statusRepo, ordersRepo);
    }

    @Test
    void start_whenTableNotFound_throwsTableNotFound() {
        when(userRepo.findById(1)).thenReturn(Optional.of(new User()));
        when(tableRepo.findById(5)).thenReturn(Optional.empty());

        assertThrows(OrderRestaurantTableNotFoundException.class, () -> service.start(1, 5));

        verify(tableRepo).findById(5);
        verifyNoInteractions(tableStatusRepo, statusRepo, ordersRepo);
    }

    @Test
    void start_whenValid_setsTableOccupied_createsNewOrder() {
        User user = new User();
        user.setId(1);
        user.setEmail("u@e.com");

        RestaurantTable table = new RestaurantTable();
        table.setId(5);
        table.setLabel("T5");

        RestaurantTableStatus occupied = new RestaurantTableStatus();
        occupied.setName("OCCUPIED");

        OrdersStatus newStatus = new OrdersStatus();
        newStatus.setName("NEW");

        Orders saved = new Orders();
        saved.setId(100);
        saved.setUser(user);
        saved.setRestaurantTable(table);
        saved.setStatus(newStatus);
        saved.setOrderItems(new ArrayList<>());
        saved.setCreateAt(LocalDateTime.now());

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(tableRepo.findById(5)).thenReturn(Optional.of(table));
        when(tableStatusRepo.findByName("OCCUPIED")).thenReturn(Optional.of(occupied));
        when(statusRepo.findByName("NEW")).thenReturn(Optional.of(newStatus));
        when(tableRepo.save(any(RestaurantTable.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ordersRepo.save(any(Orders.class))).thenReturn(saved);

        OrderResponseDto result = service.start(1, 5);

        assertEquals(100, result.getId());
        assertEquals(5, result.getRestaurantTableId());
        assertEquals("T5", result.getRestaurantTableLabel());
        assertEquals("NEW", result.getStatusName());
        assertNotNull(result.getItems());
        assertEquals(0, result.getItems().size());

        ArgumentCaptor<RestaurantTable> tableCaptor = ArgumentCaptor.forClass(RestaurantTable.class);
        verify(tableRepo).save(tableCaptor.capture());
        assertEquals("OCCUPIED", tableCaptor.getValue().getStatus().getName());

        verify(statusRepo).findByName("NEW");
        verify(ordersRepo).save(any(Orders.class));
    }

    @Test
    void pay_whenOrderNotFound_throwsNotFound() {
        when(ordersRepo.findById(1)).thenReturn(Optional.empty());

        PayOrderDto dto = new PayOrderDto();
        dto.setMethod("CASH");
        dto.setPaidAmount(10.0);

        assertThrows(OrderNotFoundException.class, () -> service.pay(1, dto));

        verify(ordersRepo).findById(1);
        verifyNoInteractions(statusRepo, tableStatusRepo, tableRepo, orderItemRepo);
    }

    @Test
    void pay_whenPaidStatusNotFound_throwsStatusNotFound() {
        Orders order = new Orders();
        order.setId(1);

        when(ordersRepo.findById(1)).thenReturn(Optional.of(order));
        when(statusRepo.findByName("PAID")).thenReturn(Optional.empty());

        PayOrderDto dto = new PayOrderDto();
        dto.setMethod("CASH");
        dto.setPaidAmount(10.0);

        assertThrows(OrderStatusNotFoundException.class, () -> service.pay(1, dto));

        verify(statusRepo).findByName("PAID");
        verifyNoInteractions(tableStatusRepo, tableRepo, orderItemRepo);
    }

    @Test
    void pay_whenOrderHasTable_setsTableAvailable_andMarksPaid() {
        OrdersStatus paid = new OrdersStatus();
        paid.setName("PAID");

        RestaurantTableStatus available = new RestaurantTableStatus();
        available.setName("AVAILABLE");

        RestaurantTable table = new RestaurantTable();
        table.setId(5);
        table.setLabel("T5");

        Orders order = new Orders();
        order.setId(1);
        order.setRestaurantTable(table);
        order.setStatus(new OrdersStatus());

        when(ordersRepo.findById(1)).thenReturn(Optional.of(order));
        when(statusRepo.findByName("PAID")).thenReturn(Optional.of(paid));
        when(tableStatusRepo.findByName("AVAILABLE")).thenReturn(Optional.of(available));
        when(tableRepo.save(any(RestaurantTable.class))).thenAnswer(inv -> inv.getArgument(0));
        when(orderItemRepo.findByOrder_Id(1)).thenReturn(List.of());
        when(ordersRepo.save(any(Orders.class))).thenAnswer(inv -> inv.getArgument(0));

        PayOrderDto dto = new PayOrderDto();
        dto.setMethod("CARD");
        dto.setPaidAmount(10.0);

        OrderResponseDto result = service.pay(1, dto);

        assertEquals("PAID", result.getStatusName());
        assertEquals(5, result.getRestaurantTableId());

        ArgumentCaptor<RestaurantTable> tableCaptor = ArgumentCaptor.forClass(RestaurantTable.class);
        verify(tableRepo).save(tableCaptor.capture());
        assertEquals("AVAILABLE", tableCaptor.getValue().getStatus().getName());

        verify(ordersRepo).save(any(Orders.class));
    }

    @Test
    void pay_whenOrderHasNoTable_marksPaidWithoutTableUpdates() {
        OrdersStatus paid = new OrdersStatus();
        paid.setName("PAID");

        Orders order = new Orders();
        order.setId(1);
        order.setRestaurantTable(null);
        order.setStatus(new OrdersStatus());

        when(ordersRepo.findById(1)).thenReturn(Optional.of(order));
        when(statusRepo.findByName("PAID")).thenReturn(Optional.of(paid));
        when(orderItemRepo.findByOrder_Id(1)).thenReturn(List.of());
        when(ordersRepo.save(any(Orders.class))).thenAnswer(inv -> inv.getArgument(0));

        PayOrderDto dto = new PayOrderDto();
        dto.setMethod("CARD");
        dto.setPaidAmount(10.0);

        OrderResponseDto result = service.pay(1, dto);

        assertEquals("PAID", result.getStatusName());
        assertNull(result.getRestaurantTableId());

        verifyNoInteractions(tableStatusRepo, tableRepo);
        verify(ordersRepo).save(any(Orders.class));
    }

    @Test
    void getPaid_mapsPaidOrders() {
        OrdersStatus paid = new OrdersStatus();
        paid.setName("PAID");

        Orders o = new Orders();
        o.setId(1);
        o.setStatus(paid);
        o.setOrderItems(new ArrayList<>());

        when(ordersRepo.findByStatus_Name("PAID")).thenReturn(List.of(o));

        List<OrderResponseDto> result = service.getPaid();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("PAID", result.get(0).getStatusName());

        verify(ordersRepo).findByStatus_Name("PAID");
    }

    @Test
    void getOpenByTable_excludesPaidAndCancelled() {
        OrdersStatus newStatus = new OrdersStatus();
        newStatus.setName("NEW");

        Orders o = new Orders();
        o.setId(1);
        o.setStatus(newStatus);
        o.setOrderItems(new ArrayList<>());

        when(ordersRepo.findByStatus_NameNotIn(List.of("PAID", "CANCELLED"))).thenReturn(List.of(o));

        List<OrderResponseDto> result = service.getOpenByTable();

        assertEquals(1, result.size());
        assertEquals("NEW", result.get(0).getStatusName());

        verify(ordersRepo).findByStatus_NameNotIn(List.of("PAID", "CANCELLED"));
    }

    @Test
    void delete_whenIdNull_throwsInvalidData() {
        assertThrows(OrderInvalidDataException.class, () -> service.delete(null));
        verifyNoInteractions(ordersRepo, orderItemRepo);
    }

    @Test
    void delete_whenOrderNotExists_throwsNotFound() {
        when(ordersRepo.existsById(1)).thenReturn(false);

        assertThrows(OrderNotFoundException.class, () -> service.delete(1));

        verify(ordersRepo).existsById(1);
        verifyNoInteractions(orderItemRepo);
    }

    @Test
    void delete_whenExists_deletesItemsThenOrder_returnsTrue() {
        when(ordersRepo.existsById(1)).thenReturn(true);

        boolean result = service.delete(1);

        assertTrue(result);
        verify(orderItemRepo).deleteByOrder_Id(1);
        verify(ordersRepo).deleteById(1);
    }

    @Test
    void updateStatus_whenInvalidInputs_throwsInvalidData() {
        OrderStatusUpdateDto dto = new OrderStatusUpdateDto();
        dto.setStatusName("PAID");

        assertThrows(OrderInvalidDataException.class, () -> service.updateStatus(null, dto));
        assertThrows(OrderInvalidDataException.class, () -> service.updateStatus(1, null));

        OrderStatusUpdateDto dto2 = new OrderStatusUpdateDto();
        dto2.setStatusName(null);
        assertThrows(OrderInvalidDataException.class, () -> service.updateStatus(1, dto2));

        verifyNoInteractions(ordersRepo, statusRepo);
    }

    @Test
    void updateStatus_whenOrderNotFound_throwsNotFound() {
        when(ordersRepo.findById(1)).thenReturn(Optional.empty());

        OrderStatusUpdateDto dto = new OrderStatusUpdateDto();
        dto.setStatusName("PAID");

        assertThrows(OrderNotFoundException.class, () -> service.updateStatus(1, dto));

        verify(ordersRepo).findById(1);
        verifyNoInteractions(statusRepo);
    }

    @Test
    void updateStatus_whenStatusNotFound_throwsStatusNotFound() {
        Orders order = new Orders();
        order.setId(1);

        when(ordersRepo.findById(1)).thenReturn(Optional.of(order));
        when(statusRepo.findByName("PAID")).thenReturn(Optional.empty());

        OrderStatusUpdateDto dto = new OrderStatusUpdateDto();
        dto.setStatusName("PAID");

        assertThrows(OrderStatusNotFoundException.class, () -> service.updateStatus(1, dto));

        verify(statusRepo).findByName("PAID");
        verify(ordersRepo).findById(1);
    }

    @Test
    void updateStatus_whenValid_setsStatusAndSaves() {
        OrdersStatus paid = new OrdersStatus();
        paid.setName("PAID");

        Orders order = new Orders();
        order.setId(1);
        order.setStatus(new OrdersStatus());
        order.setOrderItems(new ArrayList<>());
        order.setCreateAt(LocalDateTime.now());

        when(ordersRepo.findById(1)).thenReturn(Optional.of(order));
        when(statusRepo.findByName("PAID")).thenReturn(Optional.of(paid));
        when(ordersRepo.save(any(Orders.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderStatusUpdateDto dto = new OrderStatusUpdateDto();
        dto.setStatusName("PAID");

        OrderResponseDto result = service.updateStatus(1, dto);

        assertEquals(1, result.getId());
        assertEquals("PAID", result.getStatusName());

        ArgumentCaptor<Orders> captor = ArgumentCaptor.forClass(Orders.class);
        verify(ordersRepo).save(captor.capture());
        assertEquals(paid, captor.getValue().getStatus());

        verify(ordersRepo).findById(1);
        verify(statusRepo).findByName("PAID");
    }
}
