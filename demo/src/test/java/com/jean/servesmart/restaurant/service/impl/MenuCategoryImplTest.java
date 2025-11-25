package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.MenuCategory.MenuCategoryCreateDto;
import com.jean.servesmart.restaurant.dto.MenuCategory.MenuCategoryResponseDto;
import com.jean.servesmart.restaurant.dto.MenuCategory.MenuCategoryUpdateDto;

import com.jean.servesmart.restaurant.exception.menucategory.MenuCategoryAlreadyExistsException;
import com.jean.servesmart.restaurant.exception.menucategory.MenuCategoryInvalidDataException;
import com.jean.servesmart.restaurant.exception.menucategory.MenuCategoryNotFoundException;

import com.jean.servesmart.restaurant.model.MenuCategory;
import com.jean.servesmart.restaurant.repository.MenuCategoryRepository;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuCategoryImplTest {

    @Mock
    private MenuCategoryRepository repo;

    @InjectMocks
    private MenuCategoryImpl service;

    private MenuCategoryCreateDto createDto(String name, Integer position, boolean active) {
        MenuCategoryCreateDto dto = new MenuCategoryCreateDto();
        dto.setName(name);
        dto.setPosition(position);
        dto.setActive(active);
        return dto;
    }

    private MenuCategoryUpdateDto updateDto(String name, Integer position, Boolean active) {
        MenuCategoryUpdateDto dto = new MenuCategoryUpdateDto();
        dto.setName(name);
        dto.setPosition(position);
        dto.setActive(active);
        return dto;
    }

    private MenuCategory category(Integer id, String name, Integer position, boolean active) {
        MenuCategory c = new MenuCategory();
        c.setId(id);
        c.setName(name);
        c.setPosition(position);
        c.setActive(active);
        return c;
    }

    // create

    @Test
    void Create_DtoIsNull() {
        assertThrows(MenuCategoryInvalidDataException.class,
                () -> service.create(null));
        verifyNoInteractions(repo);
    }
// miss nog iets meer naam geving
//  set layaer spatie and arrange act assert
    @Test
    void Create_WhenNameIsBlank() {
        MenuCategoryCreateDto dto = createDto("   ", 1, true);
        assertThrows(MenuCategoryInvalidDataException.class,
                () -> service.create(dto));
        verifyNoInteractions(repo);
    }

    @Test
    void Create_WhenPositionIsNullOrNegative() {
        MenuCategoryCreateDto dtoNullPos = createDto("Drinks", null, true);
        MenuCategoryCreateDto dtoNegPos = createDto("Drinks", -1, true);
        assertThrows(MenuCategoryInvalidDataException.class,
                () -> service.create(dtoNullPos));
        assertThrows(MenuCategoryInvalidDataException.class,
                () -> service.create(dtoNegPos));
        verifyNoInteractions(repo);
    }

    @Test
    void create_WhenNameAlreadyExists() {
        MenuCategoryCreateDto dto = createDto("Drinks", 1, true);
        when(repo.existsByName("Drinks")).thenReturn(true);
        assertThrows(MenuCategoryAlreadyExistsException.class,
                () -> service.create(dto));
        verify(repo).existsByName("Drinks");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void create_WhenPositionAlreadyTaken() {
        MenuCategoryCreateDto dto = createDto("Drinks", 2, true);
        when(repo.existsByName("Drinks")).thenReturn(false);
        when(repo.existsByPosition(2)).thenReturn(true);
        MenuCategory savedEntity = category(10, "Drinks", 0, true);
        when(repo.save(any(MenuCategory.class))).thenReturn(savedEntity);
        MenuCategoryResponseDto result = service.create(dto);
        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals("Drinks", result.getName());
        assertEquals(0, result.getPosition()); 
        verify(repo).existsByName("Drinks");
        verify(repo).existsByPosition(2);
        verify(repo).save(any(MenuCategory.class));
    }

    @Test
    void create_WhenValidAndPositionFree() {
        MenuCategoryCreateDto dto = createDto("Drinks", 1, true);
        when(repo.existsByName("Drinks")).thenReturn(false);
        when(repo.existsByPosition(1)).thenReturn(false);
        MenuCategory savedEntity = category(5, "Drinks", 1, true);
        when(repo.save(any(MenuCategory.class))).thenReturn(savedEntity);
        MenuCategoryResponseDto result = service.create(dto);
        assertNotNull(result);
        assertEquals(5, result.getId());
        assertEquals("Drinks", result.getName());
        assertEquals(1, result.getPosition());
        assertTrue(result.isActive());
        verify(repo).existsByName("Drinks");
        verify(repo).existsByPosition(1);
        verify(repo).save(any(MenuCategory.class));
    }

    // getAll, getById, getActive

    @Test
    void getAll_ReturnMappedList() {
        List<MenuCategory> entities = Arrays.asList(
                category(1, "Drinks", 1, true),
                category(2, "Food", 2, false)
        );

        when(repo.findAll()).thenReturn(entities);
        List<MenuCategoryResponseDto> result = service.getAll();
        assertEquals(2, result.size());
        assertEquals("Drinks", result.get(0).getName());
        assertEquals("Food", result.get(1).getName());
        verify(repo).findAll();
    }

    @Test
    void getById_ReturnDto() {
        MenuCategory entity = category(1, "Drinks", 1, true);
        when(repo.findById(1)).thenReturn(Optional.of(entity));

        Optional<MenuCategoryResponseDto> result = service.getById(1);

        assertTrue(result.isPresent());
        assertEquals("Drinks", result.get().getName());
        verify(repo).findById(1);
    }

    @Test
    void GetById_ReturnEmpty() {
        when(repo.findById(99)).thenReturn(Optional.empty());
        Optional<MenuCategoryResponseDto> result = service.getById(99);
        assertTrue(result.isEmpty());
        verify(repo).findById(99);
    }

    @Test
    void GetActive_OnlyActiveMapped() {
        List<MenuCategory> entities = Arrays.asList(
                category(1, "Drinks", 1, true),
                category(2, "Food", 2, true)
        );

        when(repo.findByActiveTrue()).thenReturn(entities);
        List<MenuCategoryResponseDto> result = service.getActive();
        assertEquals(2, result.size());
        assertTrue(result.get(0).isActive());
        assertTrue(result.get(1).isActive());
        verify(repo).findByActiveTrue();
    }

    // update

    @Test
    void update_WhenIdIsNull() {
        MenuCategoryUpdateDto dto = updateDto("Drinks", 1, true);

        assertThrows(MenuCategoryInvalidDataException.class,
                () -> service.update(null, dto));

        verifyNoInteractions(repo);
    }

    @Test
    void update_WhenDtoIsNull() {
        assertThrows(MenuCategoryInvalidDataException.class,
                () -> service.update(1, null));

        verifyNoInteractions(repo);
    }

    @Test
    void Update_WhenEntityDoesNotExist() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        MenuCategoryUpdateDto dto = updateDto("New", 1, true);

        assertThrows(MenuCategoryNotFoundException.class,
                () -> service.update(99, dto));

        verify(repo).findById(99);
    }

    @Test
    void Update_WhenNewNameBlank() {
        MenuCategory existing = category(1, "Drinks", 1, true);
        when(repo.findById(1)).thenReturn(Optional.of(existing));

        MenuCategoryUpdateDto dto = updateDto("   ", null, null);

        assertThrows(MenuCategoryInvalidDataException.class,
                () -> service.update(1, dto));

        verify(repo).findById(1);
    }

    @Test
    void Update_WhenNewNameAlreadyExists() {
        MenuCategory existing = category(1, "Drinks", 1, true);
        when(repo.findById(1)).thenReturn(Optional.of(existing));

        MenuCategoryUpdateDto dto = updateDto("Food", null, null);

        when(repo.existsByName("Food")).thenReturn(true);

        assertThrows(MenuCategoryAlreadyExistsException.class,
                () -> service.update(1, dto));

        verify(repo).findById(1);
        verify(repo).existsByName("Food");
    }

    @Test
    void Update_WenNewPositionTaken() {
        MenuCategory existing = category(1, "Drinks", 1, true);
        when(repo.findById(1)).thenReturn(Optional.of(existing));

        MenuCategoryUpdateDto dto = updateDto(null, 3, null);

        when(repo.existsByPosition(3)).thenReturn(true);

        MenuCategory saved = category(1, "Drinks", 0, true);
        when(repo.save(any(MenuCategory.class))).thenReturn(saved);

        MenuCategoryResponseDto result = service.update(1, dto);

        assertEquals(0, result.getPosition());

        verify(repo).findById(1);
        verify(repo).existsByPosition(3);
        verify(repo).save(any(MenuCategory.class));
    }

    @Test
    void Update_WhenValidData() {
        MenuCategory existing = category(1, "Drinks", 1, true);
        when(repo.findById(1)).thenReturn(Optional.of(existing));

        MenuCategoryUpdateDto dto = updateDto("New Drinks", 5, false);

        when(repo.existsByName("New Drinks")).thenReturn(false);
        when(repo.existsByPosition(5)).thenReturn(false);

        MenuCategory saved = category(1, "New Drinks", 5, false);
        when(repo.save(any(MenuCategory.class))).thenReturn(saved);

        MenuCategoryResponseDto result = service.update(1, dto);

        assertEquals("New Drinks", result.getName());
        assertEquals(5, result.getPosition());
        assertFalse(result.isActive());

        verify(repo).findById(1);
        verify(repo).existsByName("New Drinks");
        verify(repo).existsByPosition(5);
        verify(repo).save(any(MenuCategory.class));
    }
    // delete

    @Test
    void Delete_WhenIdIsNull() {
        assertThrows(MenuCategoryInvalidDataException.class,
                () -> service.delete(null));

        verifyNoInteractions(repo);
    }

    @Test
    void Delete_WhenIdDoesNotExist() {
        when(repo.existsById(99)).thenReturn(false);

        assertThrows(MenuCategoryNotFoundException.class,
                () -> service.delete(99));

        verify(repo).existsById(99);
        verify(repo, never()).deleteById(anyInt());
    }

    @Test
    void Delete_WhenDeleteSucceeds() {
        when(repo.existsById(1)).thenReturn(true);

        boolean result = service.delete(1);

        assertTrue(result);
        verify(repo).existsById(1);
        verify(repo).deleteById(1);
    }

}
