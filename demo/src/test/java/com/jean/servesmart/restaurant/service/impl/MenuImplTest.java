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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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

    private MenuImpl service;

    @BeforeEach
    void setup() {
        service = new MenuImpl(menuRepo, categoryRepo);
    }

    @Test
    void create_whenDtoIsNull_throwsMenuItemInvalidDataException() {
        assertThrows(MenuItemInvalidDataException.class, () -> service.create(null));
        verifyNoInteractions(menuRepo, categoryRepo);
    }

    @Test
    void create_whenCategoryIdIsNull_throwsMenuItemInvalidDataException() {
        MenuItemDto dto = new MenuItemDto();
        dto.setCategoryId(null);
        dto.setName("Burger");
        dto.setPrice(BigDecimal.valueOf(10));

        assertThrows(MenuItemInvalidDataException.class, () -> service.create(dto));
        verifyNoInteractions(menuRepo, categoryRepo);
    }

    @Test
    void create_whenCategoryNotFound_throwsMenuItemCategoryNotFoundException() {
        MenuItemDto dto = new MenuItemDto();
        dto.setCategoryId(5);
        dto.setName("Burger");
        dto.setPrice(BigDecimal.valueOf(10));

        when(categoryRepo.findById(5)).thenReturn(Optional.empty());

        assertThrows(MenuItemCategoryNotFoundException.class, () -> service.create(dto));

        verify(categoryRepo).findById(5);
        verifyNoInteractions(menuRepo);
    }

    @Test
    void create_whenNameIsNull_throwsMenuItemInvalidDataException() {
        MenuCategory cat = new MenuCategory();
        cat.setId(1);
        cat.setName("Main");

        MenuItemDto dto = new MenuItemDto();
        dto.setCategoryId(1);
        dto.setName(null);
        dto.setPrice(BigDecimal.valueOf(10));

        when(categoryRepo.findById(1)).thenReturn(Optional.of(cat));

        assertThrows(MenuItemInvalidDataException.class, () -> service.create(dto));

        verify(categoryRepo).findById(1);
        verifyNoInteractions(menuRepo);
    }

    @Test
    void create_whenNameIsBlank_throwsMenuItemInvalidDataException() {
        MenuCategory cat = new MenuCategory();
        cat.setId(1);
        cat.setName("Main");

        MenuItemDto dto = new MenuItemDto();
        dto.setCategoryId(1);
        dto.setName("   ");
        dto.setPrice(BigDecimal.valueOf(10));

        when(categoryRepo.findById(1)).thenReturn(Optional.of(cat));

        assertThrows(MenuItemInvalidDataException.class, () -> service.create(dto));

        verify(categoryRepo).findById(1);
        verifyNoInteractions(menuRepo);
    }

    @Test
    void create_whenPriceIsNull_throwsMenuItemInvalidDataException() {
        MenuCategory cat = new MenuCategory();
        cat.setId(1);
        cat.setName("Main");

        MenuItemDto dto = new MenuItemDto();
        dto.setCategoryId(1);
        dto.setName("Burger");
        dto.setPrice(null);

        when(categoryRepo.findById(1)).thenReturn(Optional.of(cat));

        assertThrows(MenuItemInvalidDataException.class, () -> service.create(dto));

        verify(categoryRepo).findById(1);
        verifyNoInteractions(menuRepo);
    }

    @Test
    void create_whenPriceIsNegative_throwsMenuItemInvalidDataException() {
        MenuCategory cat = new MenuCategory();
        cat.setId(1);
        cat.setName("Main");

        MenuItemDto dto = new MenuItemDto();
        dto.setCategoryId(1);
        dto.setName("Burger");
        dto.setPrice(BigDecimal.valueOf(-0.01));

        when(categoryRepo.findById(1)).thenReturn(Optional.of(cat));

        assertThrows(MenuItemInvalidDataException.class, () -> service.create(dto));

        verify(categoryRepo).findById(1);
        verifyNoInteractions(menuRepo);
    }

    @Test
    void create_whenAlreadyExistsInCategory_throwsMenuItemAlreadyExistsException() {
        MenuCategory cat = new MenuCategory();
        cat.setId(1);
        cat.setName("Main");

        MenuItemDto dto = new MenuItemDto();
        dto.setCategoryId(1);
        dto.setName("  Burger  ");
        dto.setPrice(BigDecimal.valueOf(10));

        when(categoryRepo.findById(1)).thenReturn(Optional.of(cat));
        when(menuRepo.existsByCategory_IdAndName(1, "Burger")).thenReturn(true);

        assertThrows(MenuItemAlreadyExistsException.class, () -> service.create(dto));

        verify(categoryRepo).findById(1);
        verify(menuRepo).existsByCategory_IdAndName(1, "Burger");
        verifyNoMoreInteractions(menuRepo, categoryRepo);
    }

    @Test
    void create_whenDescriptionNull_setsNullDescription() {
        MenuCategory cat = new MenuCategory();
        cat.setId(1);
        cat.setName("Main");

        MenuItemDto dto = new MenuItemDto();
        dto.setCategoryId(1);
        dto.setName("Burger");
        dto.setDescription(null);
        dto.setPrice(BigDecimal.valueOf(10));
        dto.setActive(true);
        dto.setGluten(false);
        dto.setNuts(false);
        dto.setDairy(false);
        dto.setAlcohol(false);

        when(categoryRepo.findById(1)).thenReturn(Optional.of(cat));
        when(menuRepo.existsByCategory_IdAndName(1, "Burger")).thenReturn(false);

        MenuItems saved = new MenuItems();
        saved.setId(10);
        saved.setCategory(cat);
        saved.setName("Burger");
        saved.setDescription(null);
        saved.setPrice(BigDecimal.valueOf(10));
        saved.setActive(true);
        saved.setGluten(false);
        saved.setNuts(false);
        saved.setDairy(false);
        saved.setAlcohol(false);

        when(menuRepo.save(any(MenuItems.class))).thenReturn(saved);

        MenuItemDto result = service.create(dto);

        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals("Burger", result.getName());
        assertNull(result.getDescription());
        assertEquals(BigDecimal.valueOf(10), result.getPrice());
        assertEquals(1, result.getCategoryId());
        assertEquals("Main", result.getCategoryName());

        ArgumentCaptor<MenuItems> captor = ArgumentCaptor.forClass(MenuItems.class);
        verify(menuRepo).save(captor.capture());
        assertNull(captor.getValue().getDescription());

        verify(categoryRepo).findById(1);
        verify(menuRepo).existsByCategory_IdAndName(1, "Burger");
        verifyNoMoreInteractions(menuRepo, categoryRepo);
    }

    @Test
    void create_whenDescriptionBlank_setsNullDescription() {
        MenuCategory cat = new MenuCategory();
        cat.setId(1);
        cat.setName("Main");

        MenuItemDto dto = new MenuItemDto();
        dto.setCategoryId(1);
        dto.setName("Burger");
        dto.setDescription("   ");
        dto.setPrice(BigDecimal.valueOf(10));
        dto.setActive(true);

        when(categoryRepo.findById(1)).thenReturn(Optional.of(cat));
        when(menuRepo.existsByCategory_IdAndName(1, "Burger")).thenReturn(false);

        MenuItems saved = new MenuItems();
        saved.setId(10);
        saved.setCategory(cat);
        saved.setName("Burger");
        saved.setDescription(null);
        saved.setPrice(BigDecimal.valueOf(10));
        saved.setActive(true);

        when(menuRepo.save(any(MenuItems.class))).thenReturn(saved);

        MenuItemDto result = service.create(dto);

        assertNull(result.getDescription());

        ArgumentCaptor<MenuItems> captor = ArgumentCaptor.forClass(MenuItems.class);
        verify(menuRepo).save(captor.capture());
        assertNull(captor.getValue().getDescription());
    }

    @Test
    void create_whenValid_trimsName_andSavesAndMaps() {
        MenuCategory cat = new MenuCategory();
        cat.setId(1);
        cat.setName("Main");

        MenuItemDto dto = new MenuItemDto();
        dto.setCategoryId(1);
        dto.setName("  Burger  ");
        dto.setDescription("  Tasty  ");
        dto.setPrice(BigDecimal.valueOf(10));
        dto.setActive(true);
        dto.setGluten(true);
        dto.setNuts(false);
        dto.setDairy(true);
        dto.setAlcohol(false);

        when(categoryRepo.findById(1)).thenReturn(Optional.of(cat));
        when(menuRepo.existsByCategory_IdAndName(1, "Burger")).thenReturn(false);

        MenuItems saved = new MenuItems();
        saved.setId(10);
        saved.setCategory(cat);
        saved.setName("Burger");
        saved.setDescription("Tasty");
        saved.setPrice(BigDecimal.valueOf(10));
        saved.setActive(true);
        saved.setGluten(true);
        saved.setNuts(false);
        saved.setDairy(true);
        saved.setAlcohol(false);

        when(menuRepo.save(any(MenuItems.class))).thenReturn(saved);

        MenuItemDto result = service.create(dto);

        assertEquals(10, result.getId());
        assertEquals("Burger", result.getName());
        assertEquals("Tasty", result.getDescription());
        assertEquals(BigDecimal.valueOf(10), result.getPrice());
        assertTrue(result.isActive());
        assertTrue(result.isGluten());
        assertFalse(result.isNuts());
        assertTrue(result.isDairy());
        assertFalse(result.isAlcohol());
        assertEquals(1, result.getCategoryId());
        assertEquals("Main", result.getCategoryName());

        ArgumentCaptor<MenuItems> captor = ArgumentCaptor.forClass(MenuItems.class);
        verify(menuRepo).save(captor.capture());
        assertEquals("Burger", captor.getValue().getName());
        assertEquals("Tasty", captor.getValue().getDescription());
        assertEquals(cat, captor.getValue().getCategory());

        verify(categoryRepo).findById(1);
        verify(menuRepo).existsByCategory_IdAndName(1, "Burger");
        verifyNoMoreInteractions(menuRepo, categoryRepo);
    }

    @Test
    void getAll_mapsAllToDtos() {
        MenuCategory cat = new MenuCategory();
        cat.setId(1);
        cat.setName("Main");

        MenuItems i1 = new MenuItems();
        i1.setId(10);
        i1.setCategory(cat);
        i1.setName("Burger");
        i1.setDescription("Tasty");
        i1.setPrice(BigDecimal.valueOf(10));
        i1.setActive(true);

        MenuItems i2 = new MenuItems();
        i2.setId(11);
        i2.setCategory(cat);
        i2.setName("Fries");
        i2.setDescription(null);
        i2.setPrice(BigDecimal.valueOf(3));
        i2.setActive(false);

        when(menuRepo.findAll()).thenReturn(List.of(i1, i2));

        List<MenuItemDto> result = service.getAll();

        assertEquals(2, result.size());
        assertEquals(10, result.get(0).getId());
        assertEquals("Burger", result.get(0).getName());
        assertEquals("Main", result.get(0).getCategoryName());

        assertEquals(11, result.get(1).getId());
        assertEquals("Fries", result.get(1).getName());
        assertEquals("Main", result.get(1).getCategoryName());

        verify(menuRepo).findAll();
        verifyNoMoreInteractions(menuRepo, categoryRepo);
    }

    @Test
    void getById_whenIdIsNull_throwsMenuItemInvalidDataException() {
        assertThrows(MenuItemInvalidDataException.class, () -> service.getById(null));
        verifyNoInteractions(menuRepo, categoryRepo);
    }

    @Test
    void getById_whenNotFound_returnsEmptyOptional() {
        when(menuRepo.findById(99)).thenReturn(Optional.empty());

        Optional<MenuItemDto> result = service.getById(99);

        assertTrue(result.isEmpty());
        verify(menuRepo).findById(99);
        verifyNoMoreInteractions(menuRepo, categoryRepo);
    }

    @Test
    void getById_whenFound_mapsToDto() {
        MenuCategory cat = new MenuCategory();
        cat.setId(1);
        cat.setName("Main");

        MenuItems item = new MenuItems();
        item.setId(10);
        item.setCategory(cat);
        item.setName("Burger");
        item.setDescription("Tasty");
        item.setPrice(BigDecimal.valueOf(10));
        item.setActive(true);
        item.setGluten(true);
        item.setNuts(false);
        item.setDairy(true);
        item.setAlcohol(false);

        when(menuRepo.findById(10)).thenReturn(Optional.of(item));

        Optional<MenuItemDto> result = service.getById(10);

        assertTrue(result.isPresent());
        assertEquals(10, result.get().getId());
        assertEquals("Burger", result.get().getName());
        assertEquals("Main", result.get().getCategoryName());

        verify(menuRepo).findById(10);
        verifyNoMoreInteractions(menuRepo, categoryRepo);
    }

    @Test
    void getByCategory_whenCategoryIdIsNull_throwsMenuItemInvalidDataException() {
        assertThrows(MenuItemInvalidDataException.class, () -> service.getByCategory(null));
        verifyNoInteractions(menuRepo, categoryRepo);
    }

    @Test
    void getByCategory_mapsAllToDtos() {
        MenuCategory cat = new MenuCategory();
        cat.setId(1);
        cat.setName("Main");

        MenuItems i1 = new MenuItems();
        i1.setId(10);
        i1.setCategory(cat);
        i1.setName("Burger");
        i1.setPrice(BigDecimal.valueOf(10));
        i1.setActive(true);

        when(menuRepo.findByCategory_Id(1)).thenReturn(List.of(i1));

        List<MenuItemDto> result = service.getByCategory(1);

        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getId());
        assertEquals(1, result.get(0).getCategoryId());
        assertEquals("Main", result.get(0).getCategoryName());

        verify(menuRepo).findByCategory_Id(1);
        verifyNoMoreInteractions(menuRepo, categoryRepo);
    }

    @Test
    void update_whenIdIsNull_throwsMenuItemInvalidDataException() {
        MenuItemDto dto = new MenuItemDto();
        assertThrows(MenuItemInvalidDataException.class, () -> service.update(null, dto));
        verifyNoInteractions(menuRepo, categoryRepo);
    }

    @Test
    void update_whenDtoIsNull_throwsMenuItemInvalidDataException() {
        assertThrows(MenuItemInvalidDataException.class, () -> service.update(1, null));
        verifyNoInteractions(menuRepo, categoryRepo);
    }

    @Test
    void update_whenItemNotFound_throwsMenuItemNotFoundException() {
        when(menuRepo.findById(1)).thenReturn(Optional.empty());

        MenuItemDto dto = new MenuItemDto();

        assertThrows(MenuItemNotFoundException.class, () -> service.update(1, dto));

        verify(menuRepo).findById(1);
        verifyNoMoreInteractions(menuRepo, categoryRepo);
    }

    @Test
    void update_whenCategoryIdProvidedButNotFound_throwsMenuItemCategoryNotFoundException() {
        MenuCategory currentCat = new MenuCategory();
        currentCat.setId(1);
        currentCat.setName("Main");

        MenuItems item = new MenuItems();
        item.setId(10);
        item.setCategory(currentCat);
        item.setName("Burger");
        item.setPrice(BigDecimal.valueOf(10));

        when(menuRepo.findById(10)).thenReturn(Optional.of(item));
        when(categoryRepo.findById(2)).thenReturn(Optional.empty());

        MenuItemDto dto = new MenuItemDto();
        dto.setCategoryId(2);

        assertThrows(MenuItemCategoryNotFoundException.class, () -> service.update(10, dto));

        verify(menuRepo).findById(10);
        verify(categoryRepo).findById(2);
        verifyNoMoreInteractions(menuRepo, categoryRepo);
    }

    @Test
    void update_whenNameProvidedButBlank_throwsMenuItemInvalidDataException() {
        MenuCategory currentCat = new MenuCategory();
        currentCat.setId(1);
        currentCat.setName("Main");

        MenuItems item = new MenuItems();
        item.setId(10);
        item.setCategory(currentCat);
        item.setName("Burger");
        item.setPrice(BigDecimal.valueOf(10));

        when(menuRepo.findById(10)).thenReturn(Optional.of(item));

        MenuItemDto dto = new MenuItemDto();
        dto.setName("   ");

        assertThrows(MenuItemInvalidDataException.class, () -> service.update(10, dto));

        verify(menuRepo).findById(10);
        verifyNoMoreInteractions(menuRepo, categoryRepo);
    }

    @Test
    void update_whenPriceProvidedNegative_throwsMenuItemInvalidDataException() {
        MenuCategory currentCat = new MenuCategory();
        currentCat.setId(1);
        currentCat.setName("Main");

        MenuItems item = new MenuItems();
        item.setId(10);
        item.setCategory(currentCat);
        item.setName("Burger");
        item.setPrice(BigDecimal.valueOf(10));

        when(menuRepo.findById(10)).thenReturn(Optional.of(item));

        MenuItemDto dto = new MenuItemDto();
        dto.setPrice(BigDecimal.valueOf(-1));

        assertThrows(MenuItemInvalidDataException.class, () -> service.update(10, dto));

        verify(menuRepo).findById(10);
        verifyNoMoreInteractions(menuRepo, categoryRepo);
    }

    @Test
    void update_whenNameOrCategoryChange_andDuplicateExists_throwsMenuItemAlreadyExistsException() {
        MenuCategory currentCat = new MenuCategory();
        currentCat.setId(1);
        currentCat.setName("Main");

        MenuCategory targetCat = new MenuCategory();
        targetCat.setId(2);
        targetCat.setName("Starters");

        MenuItems item = new MenuItems();
        item.setId(10);
        item.setCategory(currentCat);
        item.setName("Burger");
        item.setPrice(BigDecimal.valueOf(10));

        when(menuRepo.findById(10)).thenReturn(Optional.of(item));
        when(categoryRepo.findById(2)).thenReturn(Optional.of(targetCat));
        when(menuRepo.existsByCategory_IdAndNameAndIdNot(2, "Burger", 10)).thenReturn(true);

        MenuItemDto dto = new MenuItemDto();
        dto.setCategoryId(2);
        dto.setName("Burger");

        assertThrows(MenuItemAlreadyExistsException.class, () -> service.update(10, dto));

        verify(menuRepo).findById(10);
        verify(categoryRepo).findById(2);
        verify(menuRepo).existsByCategory_IdAndNameAndIdNot(2, "Burger", 10);
        verifyNoMoreInteractions(menuRepo, categoryRepo);
    }

    @Test
    void update_whenDescriptionBlank_setsNullDescription() {
        MenuCategory currentCat = new MenuCategory();
        currentCat.setId(1);
        currentCat.setName("Main");

        MenuItems item = new MenuItems();
        item.setId(10);
        item.setCategory(currentCat);
        item.setName("Burger");
        item.setDescription("Old");
        item.setPrice(BigDecimal.valueOf(10));

        when(menuRepo.findById(10)).thenReturn(Optional.of(item));
        when(menuRepo.save(any(MenuItems.class))).thenAnswer(inv -> inv.getArgument(0));

        MenuItemDto dto = new MenuItemDto();
        dto.setDescription("   ");
        dto.setActive(item.isActive());
        dto.setGluten(item.isGluten());
        dto.setNuts(item.isNuts());
        dto.setDairy(item.isDairy());
        dto.setAlcohol(item.isAlcohol());

        MenuItemDto result = service.update(10, dto);

        assertNull(result.getDescription());

        ArgumentCaptor<MenuItems> captor = ArgumentCaptor.forClass(MenuItems.class);
        verify(menuRepo).save(captor.capture());
        assertNull(captor.getValue().getDescription());
    }

    @Test
    void update_whenValid_updatesFieldsAndSaves() {
        MenuCategory currentCat = new MenuCategory();
        currentCat.setId(1);
        currentCat.setName("Main");

        MenuCategory targetCat = new MenuCategory();
        targetCat.setId(2);
        targetCat.setName("Starters");

        MenuItems item = new MenuItems();
        item.setId(10);
        item.setCategory(currentCat);
        item.setName("Burger");
        item.setDescription("Old");
        item.setPrice(BigDecimal.valueOf(10));
        item.setActive(false);
        item.setGluten(false);
        item.setNuts(false);
        item.setDairy(false);
        item.setAlcohol(false);

        when(menuRepo.findById(10)).thenReturn(Optional.of(item));
        when(categoryRepo.findById(2)).thenReturn(Optional.of(targetCat));
        when(menuRepo.existsByCategory_IdAndNameAndIdNot(2, "New Burger", 10)).thenReturn(false);
        when(menuRepo.save(any(MenuItems.class))).thenAnswer(inv -> inv.getArgument(0));

        MenuItemDto dto = new MenuItemDto();
        dto.setCategoryId(2);
        dto.setName("  New Burger  ");
        dto.setDescription("  New Desc  ");
        dto.setPrice(BigDecimal.valueOf(12));
        dto.setActive(true);
        dto.setGluten(true);
        dto.setNuts(true);
        dto.setDairy(true);
        dto.setAlcohol(true);

        MenuItemDto result = service.update(10, dto);

        assertEquals("New Burger", result.getName());
        assertEquals("New Desc", result.getDescription());
        assertEquals(BigDecimal.valueOf(12), result.getPrice());
        assertEquals(2, result.getCategoryId());
        assertEquals("Starters", result.getCategoryName());
        assertTrue(result.isActive());
        assertTrue(result.isGluten());
        assertTrue(result.isNuts());
        assertTrue(result.isDairy());
        assertTrue(result.isAlcohol());

        ArgumentCaptor<MenuItems> captor = ArgumentCaptor.forClass(MenuItems.class);
        verify(menuRepo).save(captor.capture());
        assertEquals("New Burger", captor.getValue().getName());
        assertEquals("New Desc", captor.getValue().getDescription());
        assertEquals(BigDecimal.valueOf(12), captor.getValue().getPrice());
        assertEquals(targetCat, captor.getValue().getCategory());
        assertTrue(captor.getValue().isActive());

        verify(menuRepo).findById(10);
        verify(categoryRepo).findById(2);
        verify(menuRepo).existsByCategory_IdAndNameAndIdNot(2, "New Burger", 10);
        verify(menuRepo).save(any(MenuItems.class));
        verifyNoMoreInteractions(menuRepo, categoryRepo);
    }

    @Test
    void delete_whenIdIsNull_throwsMenuItemInvalidDataException() {
        assertThrows(MenuItemInvalidDataException.class, () -> service.delete(null));
        verifyNoInteractions(menuRepo, categoryRepo);
    }

    @Test
    void delete_whenNotExists_throwsMenuItemNotFoundException() {
        when(menuRepo.existsById(9)).thenReturn(false);

        assertThrows(MenuItemNotFoundException.class, () -> service.delete(9));

        verify(menuRepo).existsById(9);
        verifyNoMoreInteractions(menuRepo, categoryRepo);
    }

    @Test
    void delete_whenExists_deletesAndReturnsTrue() {
        when(menuRepo.existsById(9)).thenReturn(true);

        boolean result = service.delete(9);

        assertTrue(result);
        verify(menuRepo).existsById(9);
        verify(menuRepo).deleteById(9);
        verifyNoMoreInteractions(menuRepo, categoryRepo);
    }
}
