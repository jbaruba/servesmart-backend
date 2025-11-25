package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.Menu.MenuItemDto;
import com.jean.servesmart.restaurant.exception.menuitem.MenuItemAlreadyExistsException;
import com.jean.servesmart.restaurant.exception.menuitem.MenuItemCategoryNotFoundException;
import com.jean.servesmart.restaurant.exception.menuitem.MenuItemInvalidDataException;
import com.jean.servesmart.restaurant.exception.menuitem.MenuItemNotFoundException;
import com.jean.servesmart.restaurant.model.MenuCategory;
import com.jean.servesmart.restaurant.model.MenuItems;
import com.jean.servesmart.restaurant.repository.MenuCategoryRepository;
import com.jean.servesmart.restaurant.repository.MenuItemsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuImplTest {

    @Mock
    private MenuItemsRepository menuRepo;

    @Mock
    private MenuCategoryRepository categoryRepo;

    @InjectMocks
    private MenuImpl service;

    private MenuCategory category(Integer id, String name) {
        MenuCategory c = new MenuCategory();
        c.setId(id);
        c.setName(name);
        return c;
    }

    private MenuItems item(Integer id, String name, MenuCategory category, String description, BigDecimal price,boolean active, boolean gluten, boolean nuts, boolean dairy, boolean alcohol) {
        MenuItems m = new MenuItems();
        m.setId(id);
        m.setName(name);
        m.setCategory(category);
        m.setDescription(description);
        m.setPrice(price);
        m.setActive(active);
        m.setGluten(gluten);
        m.setNuts(nuts);
        m.setDairy(dairy);
        m.setAlcohol(alcohol);
        return m;
    }

    private MenuItemDto dto(Integer id, Integer categoryId, String name, String description, BigDecimal price, boolean active, boolean gluten, boolean nuts, boolean dairy, boolean alcohol) {
        MenuItemDto dto = new MenuItemDto();
        dto.setId(id);
        dto.setCategoryId(categoryId);
        dto.setName(name);
        dto.setDescription(description);
        dto.setPrice(price);
        dto.setActive(active);
        dto.setGluten(gluten);
        dto.setNuts(nuts);
        dto.setDairy(dairy);
        dto.setAlcohol(alcohol);
        return dto;
    }

    // create

    @Test
    void create_DtoIsNull() {
        assertThrows(MenuItemInvalidDataException.class,
                () -> service.create(null));

        verifyNoInteractions(menuRepo, categoryRepo);
    }

    @Test
    void create_CategoryIdIsNull() {
        MenuItemDto dto = dto(null, null, "Beer", "desc",
                BigDecimal.TEN, true, false, false, false, false);

        assertThrows(MenuItemInvalidDataException.class,
                () -> service.create(dto));

        verifyNoInteractions(menuRepo, categoryRepo);
    }

    @Test
    void create_CategoryDoesNotExist() {
        MenuItemDto dto = dto(null, 1, "Beer", "desc",
                BigDecimal.TEN, true, false, false, false, false);

        when(categoryRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(MenuItemCategoryNotFoundException.class,
                () -> service.create(dto));

        verify(categoryRepo).findById(1);
        verifyNoInteractions(menuRepo);
    }

    @Test
    void create_NameBlank() {
        MenuItemDto dto = dto(null, 1, "   ", "desc",
                BigDecimal.TEN, true, false, false, false, false);

        MenuCategory cat = category(1, "Drinks");
        when(categoryRepo.findById(1)).thenReturn(Optional.of(cat));

        assertThrows(MenuItemInvalidDataException.class,
                () -> service.create(dto));

        verify(categoryRepo).findById(1);
        verifyNoInteractions(menuRepo);
    }

    @Test
    void create_PriceNegativeOrNull() {
        MenuCategory cat = category(1, "Drinks");
        when(categoryRepo.findById(1)).thenReturn(Optional.of(cat));

        MenuItemDto nullPriceDto = dto(null, 1, "Beer", "desc",
                null, true, false, false, false, false);
        MenuItemDto negativePriceDto = dto(null, 1, "Beer", "desc",
                BigDecimal.valueOf(-1), true, false, false, false, false);

        assertThrows(MenuItemInvalidDataException.class,
                () -> service.create(nullPriceDto));
        assertThrows(MenuItemInvalidDataException.class,
                () -> service.create(negativePriceDto));

        verify(categoryRepo, times(2)).findById(1);
        verifyNoInteractions(menuRepo);
    }

    @Test
    void create_NameExistsInCategory() {
        MenuCategory cat = category(1, "Drinks");
        when(categoryRepo.findById(1)).thenReturn(Optional.of(cat));

        MenuItemDto dto = dto(null, 1, "Beer", "desc",
                BigDecimal.TEN, true, false, false, false, false);

        when(menuRepo.existsByCategory_IdAndName(1, "Beer")).thenReturn(true);

        assertThrows(MenuItemAlreadyExistsException.class,
                () -> service.create(dto));

        verify(categoryRepo).findById(1);
        verify(menuRepo).existsByCategory_IdAndName(1, "Beer");
        verify(menuRepo, never()).save(any());
    }

    @Test
    void create_TrimNameAndDescriptionAndSave_whenValid() {
        MenuCategory cat = category(1, "Drinks");
        when(categoryRepo.findById(1)).thenReturn(Optional.of(cat));

        MenuItemDto dto = dto(null, 1, "  Beer  ", "  nice drink  ",
                BigDecimal.TEN, true, true, false, false, false);

        when(menuRepo.existsByCategory_IdAndName(1, "Beer")).thenReturn(false);

        MenuItems saved = item(10, "Beer", cat, "nice drink", BigDecimal.TEN,
                true, true, false, false, false);
        when(menuRepo.save(any(MenuItems.class))).thenReturn(saved);

        MenuItemDto result = service.create(dto);

        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals("Beer", result.getName());
        assertEquals("nice drink", result.getDescription());
        assertEquals(BigDecimal.TEN, result.getPrice());
        assertEquals(1, result.getCategoryId());
        assertEquals("Drinks", result.getCategoryName());

        verify(categoryRepo).findById(1);
        verify(menuRepo).existsByCategory_IdAndName(1, "Beer");
        verify(menuRepo).save(any(MenuItems.class));
    }

    // getAll, getById, getByCategory

    @Test
    void getAll_sReturnMappedList() {
        MenuCategory drinks = category(1, "Drinks");
        MenuCategory food = category(2, "Food");

        List<MenuItems> items = Arrays.asList(
                item(1, "Beer", drinks, null, BigDecimal.TEN,
                        true, false, false, false, false),
                item(2, "Burger", food, "tasty", BigDecimal.valueOf(12),
                        true, false, true, true, false)
        );

        when(menuRepo.findAll()).thenReturn(items);

        List<MenuItemDto> result = service.getAll();

        assertEquals(2, result.size());
        assertEquals("Beer", result.get(0).getName());
        assertEquals("Burger", result.get(1).getName());
        assertEquals("Drinks", result.get(0).getCategoryName());
        assertEquals("Food", result.get(1).getCategoryName());

        verify(menuRepo).findAll();
    }

    @Test
    void getById_IdNull() {
        assertThrows(MenuItemInvalidDataException.class,
                () -> service.getById(null));

        verifyNoInteractions(menuRepo);
    }

    @Test
    void getById_NotFound() {
        when(menuRepo.findById(99)).thenReturn(Optional.empty());

        Optional<MenuItemDto> result = service.getById(99);

        assertTrue(result.isEmpty());
        verify(menuRepo).findById(99);
    }

    @Test
    void getById_Found() {
        MenuCategory cat = category(1, "Drinks");
        MenuItems entity = item(5, "Beer", cat, "desc",
                BigDecimal.TEN, true, false, false, false, false);

        when(menuRepo.findById(5)).thenReturn(Optional.of(entity));

        Optional<MenuItemDto> result = service.getById(5);

        assertTrue(result.isPresent());
        assertEquals("Beer", result.get().getName());
        assertEquals("Drinks", result.get().getCategoryName());

        verify(menuRepo).findById(5);
    }

    @Test
    void getByCategory_CategoryIdNull() {
        assertThrows(MenuItemInvalidDataException.class,
                () -> service.getByCategory(null));

        verifyNoInteractions(menuRepo);
    }

    @Test
    void getByCategory_MappedList() {
        MenuCategory drinks = category(1, "Drinks");
        List<MenuItems> items = Arrays.asList(
                item(1, "Beer", drinks, null, BigDecimal.TEN,
                        true, false, false, false, false),
                item(2, "Wine", drinks, "red", BigDecimal.valueOf(15),
                        true, false, false, false, true)
        );

        when(menuRepo.findByCategory_Id(1)).thenReturn(items);

        List<MenuItemDto> result = service.getByCategory(1);

        assertEquals(2, result.size());
        assertEquals("Beer", result.get(0).getName());
        assertEquals("Wine", result.get(1).getName());
        assertEquals("Drinks", result.get(0).getCategoryName());

        verify(menuRepo).findByCategory_Id(1);
    }

    // update

    @Test
    void update_IdNull() {
        MenuItemDto dto = dto(null, 1, "Beer", null,
                BigDecimal.TEN, true, false, false, false, false);

        assertThrows(MenuItemInvalidDataException.class,
                () -> service.update(null, dto));

        verifyNoInteractions(menuRepo, categoryRepo);
    }

    @Test
    void update_DtoNull() {
        assertThrows(MenuItemInvalidDataException.class,
                () -> service.update(1, null));

        verifyNoInteractions(menuRepo, categoryRepo);
    }

    @Test
    void update_ItemDoesNotExist() {
        when(menuRepo.findById(99)).thenReturn(Optional.empty());

        MenuItemDto dto = dto(null, 1, "Beer", null,
                BigDecimal.TEN, true, false, false, false, false);

        assertThrows(MenuItemNotFoundException.class,
                () -> service.update(99, dto));

        verify(menuRepo).findById(99);
    }

    @Test
    void update_ChangingToUnknownCategory() {
        MenuCategory drinks = category(1, "Drinks");
        MenuItems existing = item(5, "Beer", drinks, null,
                BigDecimal.TEN, true, false, false, false, false);

        when(menuRepo.findById(5)).thenReturn(Optional.of(existing));
        when(categoryRepo.findById(2)).thenReturn(Optional.empty());

        MenuItemDto dto = dto(null, 2, "Beer", null,
                BigDecimal.TEN, true, false, false, false, false);

        assertThrows(MenuItemCategoryNotFoundException.class,
                () -> service.update(5, dto));

        verify(menuRepo).findById(5);
        verify(categoryRepo).findById(2);
    }

    @Test
    void update_NewNameBlank() {
        MenuCategory drinks = category(1, "Drinks");
        MenuItems existing = item(5, "Beer", drinks, null,
                BigDecimal.TEN, true, false, false, false, false);

        when(menuRepo.findById(5)).thenReturn(Optional.of(existing));

        MenuItemDto dto = dto(null, null, "   ", null,
                BigDecimal.TEN, true, false, false, false, false);

        assertThrows(MenuItemInvalidDataException.class,
                () -> service.update(5, dto));

        verify(menuRepo).findById(5);
        verifyNoInteractions(categoryRepo);
    }

    @Test
    void update_NameOrCategoryChangeConflicts() {
        MenuCategory drinks = category(1, "Drinks");
        MenuCategory food = category(2, "Food");

        MenuItems existing = item(5, "Beer", drinks, null,
                BigDecimal.TEN, true, false, false, false, false);

        when(menuRepo.findById(5)).thenReturn(Optional.of(existing));
        when(categoryRepo.findById(2)).thenReturn(Optional.of(food));

        MenuItemDto dto = dto(null, 2, "Beer", null,
                BigDecimal.TEN, true, false, false, false, false);

        when(menuRepo.existsByCategory_IdAndNameAndIdNot(2, "Beer", 5))
                .thenReturn(true);

        assertThrows(MenuItemAlreadyExistsException.class,
                () -> service.update(5, dto));

        verify(menuRepo).findById(5);
        verify(categoryRepo).findById(2);
        verify(menuRepo).existsByCategory_IdAndNameAndIdNot(2, "Beer", 5);
        verify(menuRepo, never()).save(any());
    }

    @Test
    void update_PriceNegative() {
        MenuCategory drinks = category(1, "Drinks");
        MenuItems existing = item(5, "Beer", drinks, null,
                BigDecimal.TEN, true, false, false, false, false);

        when(menuRepo.findById(5)).thenReturn(Optional.of(existing));

        MenuItemDto dto = dto(null, null, "Beer", null,
                BigDecimal.valueOf(-1), true, false, false, false, false);

        assertThrows(MenuItemInvalidDataException.class,
                () -> service.update(5, dto));

        verify(menuRepo).findById(5);
    }

    @Test
    void update_Valid() {
        MenuCategory drinks = category(1, "Drinks");
        MenuCategory food = category(2, "Food");

        MenuItems existing = item(5, "Beer", drinks, "old",
                BigDecimal.TEN, true, false, false, false, false);

        when(menuRepo.findById(5)).thenReturn(Optional.of(existing));
        when(categoryRepo.findById(2)).thenReturn(Optional.of(food));
        when(menuRepo.existsByCategory_IdAndNameAndIdNot(2, "New Beer", 5))
                .thenReturn(false);

        MenuItemDto dto = dto(null, 2, "  New Beer  ", "  new desc  ",
                BigDecimal.valueOf(12), false, true, true, false, true);

        MenuItems updatedEntity = item(5, "New Beer", food, "new desc",
                BigDecimal.valueOf(12), false, true, true, false, true);

        when(menuRepo.save(any(MenuItems.class))).thenReturn(updatedEntity);

        MenuItemDto result = service.update(5, dto);

        assertEquals(5, result.getId());
        assertEquals("New Beer", result.getName());
        assertEquals("new desc", result.getDescription());
        assertEquals(BigDecimal.valueOf(12), result.getPrice());
        assertEquals(2, result.getCategoryId());
        assertEquals("Food", result.getCategoryName());
        assertFalse(result.isActive());
        assertTrue(result.isGluten());
        assertTrue(result.isNuts());
        assertFalse(result.isDairy());
        assertTrue(result.isAlcohol());

        verify(menuRepo).findById(5);
        verify(categoryRepo).findById(2);
        verify(menuRepo).existsByCategory_IdAndNameAndIdNot(2, "New Beer", 5);
        verify(menuRepo).save(any(MenuItems.class));
    }

    // delete

    @Test
    void Delete_IdNull() {
        assertThrows(MenuItemInvalidDataException.class,
                () -> service.delete(null));

        verifyNoInteractions(menuRepo);
    }

    @Test
    void Delete_ItemDoesNotExist() {
        when(menuRepo.existsById(99)).thenReturn(false);

        assertThrows(MenuItemNotFoundException.class,
                () -> service.delete(99));

        verify(menuRepo).existsById(99);
        verify(menuRepo, never()).deleteById(anyInt());
        }

    @Test
    void Delete_DeleteSucceeds() {
        when(menuRepo.existsById(5)).thenReturn(true);

        boolean result = service.delete(5);

        assertTrue(result);
        verify(menuRepo).existsById(5);
        verify(menuRepo).deleteById(5);
    }
}
