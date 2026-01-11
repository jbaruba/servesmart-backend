package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.MenuCategory.MenuCategoryCreateDto;
import com.jean.servesmart.restaurant.dto.MenuCategory.MenuCategoryResponseDto;
import com.jean.servesmart.restaurant.dto.MenuCategory.MenuCategoryUpdateDto;
import com.jean.servesmart.restaurant.exception.menucategory.MenuCategoryAlreadyExistsException;
import com.jean.servesmart.restaurant.exception.menucategory.MenuCategoryInvalidDataException;
import com.jean.servesmart.restaurant.exception.menucategory.MenuCategoryNotFoundException;
import com.jean.servesmart.restaurant.model.MenuCategory;
import com.jean.servesmart.restaurant.repository.MenuCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuCategoryImplTest {

    @Mock
    private MenuCategoryRepository repo;

    private MenuCategoryImpl service;

    @BeforeEach
    void setup() {
        service = new MenuCategoryImpl(repo);
    }

    @Test
    void create_whenDtoIsNull_throwsMenuCategoryInvalidDataException() {
        assertThrows(MenuCategoryInvalidDataException.class, () -> service.create(null));
        verifyNoInteractions(repo);
    }

    @Test
    void create_whenNameIsNull_throwsMenuCategoryInvalidDataException() {
        MenuCategoryCreateDto dto = new MenuCategoryCreateDto();
        dto.setName(null);
        dto.setPosition(1);
        dto.setActive(true);

        assertThrows(MenuCategoryInvalidDataException.class, () -> service.create(dto));
        verifyNoInteractions(repo);
    }

    @Test
    void create_whenNameIsBlank_throwsMenuCategoryInvalidDataException() {
        MenuCategoryCreateDto dto = new MenuCategoryCreateDto();
        dto.setName("   ");
        dto.setPosition(1);
        dto.setActive(true);

        assertThrows(MenuCategoryInvalidDataException.class, () -> service.create(dto));
        verifyNoInteractions(repo);
    }

    @Test
    void create_whenPositionIsNull_throwsMenuCategoryInvalidDataException() {
        MenuCategoryCreateDto dto = new MenuCategoryCreateDto();
        dto.setName("Starters");
        dto.setPosition(null);
        dto.setActive(true);

        assertThrows(MenuCategoryInvalidDataException.class, () -> service.create(dto));
        verifyNoInteractions(repo);
    }

    @Test
    void create_whenPositionIsNegative_throwsMenuCategoryInvalidDataException() {
        MenuCategoryCreateDto dto = new MenuCategoryCreateDto();
        dto.setName("Starters");
        dto.setPosition(-1);
        dto.setActive(true);

        assertThrows(MenuCategoryInvalidDataException.class, () -> service.create(dto));
        verifyNoInteractions(repo);
    }

    @Test
    void create_whenNameAlreadyExists_throwsMenuCategoryAlreadyExistsException() {
        MenuCategoryCreateDto dto = new MenuCategoryCreateDto();
        dto.setName("  Starters  ");
        dto.setPosition(1);
        dto.setActive(true);

        when(repo.existsByName("Starters")).thenReturn(true);

        assertThrows(MenuCategoryAlreadyExistsException.class, () -> service.create(dto));

        verify(repo).existsByName("Starters");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void create_whenPositionConflicts_setsPositionToZero() {
        MenuCategoryCreateDto dto = new MenuCategoryCreateDto();
        dto.setName("Starters");
        dto.setPosition(2);
        dto.setActive(true);

        when(repo.existsByName("Starters")).thenReturn(false);
        when(repo.existsByPosition(2)).thenReturn(true);

        MenuCategory saved = new MenuCategory();
        saved.setId(10);
        saved.setName("Starters");
        saved.setPosition(0);
        saved.setActive(true);

        when(repo.save(any(MenuCategory.class))).thenReturn(saved);

        MenuCategoryResponseDto result = service.create(dto);

        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals("Starters", result.getName());
        assertEquals(0, result.getPosition());
        assertTrue(result.isActive());

        ArgumentCaptor<MenuCategory> captor = ArgumentCaptor.forClass(MenuCategory.class);
        verify(repo).save(captor.capture());
        assertEquals("Starters", captor.getValue().getName());
        assertEquals(0, captor.getValue().getPosition());
        assertTrue(captor.getValue().isActive());

        verify(repo).existsByName("Starters");
        verify(repo).existsByPosition(2);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void create_whenPositionIsZero_doesNotCheckPositionConflict() {
        MenuCategoryCreateDto dto = new MenuCategoryCreateDto();
        dto.setName("Starters");
        dto.setPosition(0);
        dto.setActive(false);

        when(repo.existsByName("Starters")).thenReturn(false);

        MenuCategory saved = new MenuCategory();
        saved.setId(1);
        saved.setName("Starters");
        saved.setPosition(0);
        saved.setActive(false);

        when(repo.save(any(MenuCategory.class))).thenReturn(saved);

        MenuCategoryResponseDto result = service.create(dto);

        assertEquals(0, result.getPosition());
        assertFalse(result.isActive());

        verify(repo).existsByName("Starters");
        verify(repo, never()).existsByPosition(anyInt());
        verify(repo).save(any(MenuCategory.class));
        verifyNoMoreInteractions(repo);
    }

    @Test
    void create_whenValid_trimsName_andSaves() {
        MenuCategoryCreateDto dto = new MenuCategoryCreateDto();
        dto.setName("  Drinks  ");
        dto.setPosition(3);
        dto.setActive(true);

        when(repo.existsByName("Drinks")).thenReturn(false);
        when(repo.existsByPosition(3)).thenReturn(false);

        MenuCategory saved = new MenuCategory();
        saved.setId(7);
        saved.setName("Drinks");
        saved.setPosition(3);
        saved.setActive(true);

        when(repo.save(any(MenuCategory.class))).thenReturn(saved);

        MenuCategoryResponseDto result = service.create(dto);

        assertNotNull(result);
        assertEquals(7, result.getId());
        assertEquals("Drinks", result.getName());
        assertEquals(3, result.getPosition());
        assertTrue(result.isActive());

        ArgumentCaptor<MenuCategory> captor = ArgumentCaptor.forClass(MenuCategory.class);
        verify(repo).save(captor.capture());
        assertEquals("Drinks", captor.getValue().getName());
        assertEquals(3, captor.getValue().getPosition());
        assertTrue(captor.getValue().isActive());

        verify(repo).existsByName("Drinks");
        verify(repo).existsByPosition(3);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getAll_mapsAllToResponseDtos() {
        MenuCategory c1 = new MenuCategory();
        c1.setId(1);
        c1.setName("Starters");
        c1.setPosition(1);
        c1.setActive(true);

        MenuCategory c2 = new MenuCategory();
        c2.setId(2);
        c2.setName("Desserts");
        c2.setPosition(2);
        c2.setActive(false);

        when(repo.findAll()).thenReturn(List.of(c1, c2));

        List<MenuCategoryResponseDto> result = service.getAll();

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("Starters", result.get(0).getName());
        assertEquals(1, result.get(0).getPosition());
        assertTrue(result.get(0).isActive());

        assertEquals(2, result.get(1).getId());
        assertEquals("Desserts", result.get(1).getName());
        assertEquals(2, result.get(1).getPosition());
        assertFalse(result.get(1).isActive());

        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getById_whenNotFound_returnsEmptyOptional() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        Optional<MenuCategoryResponseDto> result = service.getById(99);

        assertTrue(result.isEmpty());
        verify(repo).findById(99);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getById_whenFound_mapsToDto() {
        MenuCategory c = new MenuCategory();
        c.setId(5);
        c.setName("Main");
        c.setPosition(1);
        c.setActive(true);

        when(repo.findById(5)).thenReturn(Optional.of(c));

        Optional<MenuCategoryResponseDto> result = service.getById(5);

        assertTrue(result.isPresent());
        assertEquals(5, result.get().getId());
        assertEquals("Main", result.get().getName());
        assertEquals(1, result.get().getPosition());
        assertTrue(result.get().isActive());

        verify(repo).findById(5);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void update_whenIdIsNull_throwsMenuCategoryInvalidDataException() {
        MenuCategoryUpdateDto dto = new MenuCategoryUpdateDto();
        assertThrows(MenuCategoryInvalidDataException.class, () -> service.update(null, dto));
        verifyNoInteractions(repo);
    }

    @Test
    void update_whenDtoIsNull_throwsMenuCategoryInvalidDataException() {
        assertThrows(MenuCategoryInvalidDataException.class, () -> service.update(1, null));
        verifyNoInteractions(repo);
    }

    @Test
    void update_whenCategoryNotFound_throwsMenuCategoryNotFoundException() {
        MenuCategoryUpdateDto dto = new MenuCategoryUpdateDto();
        when(repo.findById(1)).thenReturn(Optional.empty());

        assertThrows(MenuCategoryNotFoundException.class, () -> service.update(1, dto));

        verify(repo).findById(1);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void update_whenNameProvidedButBlank_throwsMenuCategoryInvalidDataException() {
        MenuCategory category = new MenuCategory();
        category.setId(1);
        category.setName("Starters");
        category.setPosition(1);
        category.setActive(true);

        when(repo.findById(1)).thenReturn(Optional.of(category));

        MenuCategoryUpdateDto dto = new MenuCategoryUpdateDto();
        dto.setName("   ");

        assertThrows(MenuCategoryInvalidDataException.class, () -> service.update(1, dto));

        verify(repo).findById(1);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void update_whenNewNameExists_throwsMenuCategoryAlreadyExistsException() {
        MenuCategory category = new MenuCategory();
        category.setId(1);
        category.setName("Starters");
        category.setPosition(1);
        category.setActive(true);

        when(repo.findById(1)).thenReturn(Optional.of(category));
        when(repo.existsByName("Desserts")).thenReturn(true);

        MenuCategoryUpdateDto dto = new MenuCategoryUpdateDto();
        dto.setName("  Desserts  ");

        assertThrows(MenuCategoryAlreadyExistsException.class, () -> service.update(1, dto));

        verify(repo).findById(1);
        verify(repo).existsByName("Desserts");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void update_whenPositionIsNegative_throwsMenuCategoryInvalidDataException() {
        MenuCategory category = new MenuCategory();
        category.setId(1);
        category.setName("Starters");
        category.setPosition(1);
        category.setActive(true);

        when(repo.findById(1)).thenReturn(Optional.of(category));

        MenuCategoryUpdateDto dto = new MenuCategoryUpdateDto();
        dto.setPosition(-2);

        assertThrows(MenuCategoryInvalidDataException.class, () -> service.update(1, dto));

        verify(repo).findById(1);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void update_whenPositionConflicts_setsPositionToZero() {
        MenuCategory category = new MenuCategory();
        category.setId(1);
        category.setName("Starters");
        category.setPosition(1);
        category.setActive(true);

        when(repo.findById(1)).thenReturn(Optional.of(category));
        when(repo.existsByPosition(2)).thenReturn(true);

        MenuCategoryUpdateDto dto = new MenuCategoryUpdateDto();
        dto.setPosition(2);

        MenuCategory saved = new MenuCategory();
        saved.setId(1);
        saved.setName("Starters");
        saved.setPosition(0);
        saved.setActive(true);

        when(repo.save(any(MenuCategory.class))).thenReturn(saved);

        MenuCategoryResponseDto result = service.update(1, dto);

        assertEquals(0, result.getPosition());

        ArgumentCaptor<MenuCategory> captor = ArgumentCaptor.forClass(MenuCategory.class);
        verify(repo).save(captor.capture());
        assertEquals(0, captor.getValue().getPosition());

        verify(repo).findById(1);
        verify(repo).existsByPosition(2);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void update_whenPositionSame_doesNotCheckPositionConflict() {
        MenuCategory category = new MenuCategory();
        category.setId(1);
        category.setName("Starters");
        category.setPosition(2);
        category.setActive(true);

        when(repo.findById(1)).thenReturn(Optional.of(category));

        MenuCategoryUpdateDto dto = new MenuCategoryUpdateDto();
        dto.setPosition(2);

        when(repo.save(any(MenuCategory.class))).thenAnswer(inv -> inv.getArgument(0));

        MenuCategoryResponseDto result = service.update(1, dto);

        assertEquals(2, result.getPosition());

        verify(repo).findById(1);
        verify(repo, never()).existsByPosition(anyInt());
        verify(repo).save(any(MenuCategory.class));
        verifyNoMoreInteractions(repo);
    }

    @Test
    void update_whenValid_updatesFieldsAndSaves() {
        MenuCategory category = new MenuCategory();
        category.setId(1);
        category.setName("Starters");
        category.setPosition(1);
        category.setActive(false);

        when(repo.findById(1)).thenReturn(Optional.of(category));
        when(repo.existsByName("Drinks")).thenReturn(false);
        when(repo.existsByPosition(3)).thenReturn(false);

        MenuCategoryUpdateDto dto = new MenuCategoryUpdateDto();
        dto.setName("  Drinks  ");
        dto.setPosition(3);
        dto.setActive(true);

        MenuCategory saved = new MenuCategory();
        saved.setId(1);
        saved.setName("Drinks");
        saved.setPosition(3);
        saved.setActive(true);

        when(repo.save(any(MenuCategory.class))).thenReturn(saved);

        MenuCategoryResponseDto result = service.update(1, dto);

        assertEquals(1, result.getId());
        assertEquals("Drinks", result.getName());
        assertEquals(3, result.getPosition());
        assertTrue(result.isActive());

        verify(repo).findById(1);
        verify(repo).existsByName("Drinks");
        verify(repo).existsByPosition(3);
        verify(repo).save(any(MenuCategory.class));
        verifyNoMoreInteractions(repo);
    }

    @Test
    void update_whenOnlyActiveProvided_updatesActiveOnly() {
        MenuCategory category = new MenuCategory();
        category.setId(1);
        category.setName("Starters");
        category.setPosition(1);
        category.setActive(false);

        when(repo.findById(1)).thenReturn(Optional.of(category));
        when(repo.save(any(MenuCategory.class))).thenAnswer(inv -> inv.getArgument(0));

        MenuCategoryUpdateDto dto = new MenuCategoryUpdateDto();
        dto.setActive(true);

        MenuCategoryResponseDto result = service.update(1, dto);

        assertEquals("Starters", result.getName());
        assertEquals(1, result.getPosition());
        assertTrue(result.isActive());

        verify(repo).findById(1);
        verify(repo).save(any(MenuCategory.class));
        verifyNoMoreInteractions(repo);
    }

    @Test
    void delete_whenIdIsNull_throwsMenuCategoryInvalidDataException() {
        assertThrows(MenuCategoryInvalidDataException.class, () -> service.delete(null));
        verifyNoInteractions(repo);
    }

    @Test
    void delete_whenNotExists_throwsMenuCategoryNotFoundException() {
        when(repo.existsById(9)).thenReturn(false);

        assertThrows(MenuCategoryNotFoundException.class, () -> service.delete(9));

        verify(repo).existsById(9);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void delete_whenExists_deletesAndReturnsTrue() {
        when(repo.existsById(9)).thenReturn(true);

        boolean result = service.delete(9);

        assertTrue(result);
        verify(repo).existsById(9);
        verify(repo).deleteById(9);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getActive_mapsActiveToDtos() {
        MenuCategory c1 = new MenuCategory();
        c1.setId(1);
        c1.setName("Starters");
        c1.setPosition(1);
        c1.setActive(true);

        MenuCategory c2 = new MenuCategory();
        c2.setId(2);
        c2.setName("Drinks");
        c2.setPosition(2);
        c2.setActive(true);

        when(repo.findByActiveTrue()).thenReturn(List.of(c1, c2));

        List<MenuCategoryResponseDto> result = service.getActive();

        assertEquals(2, result.size());
        assertEquals("Starters", result.get(0).getName());
        assertTrue(result.get(0).isActive());
        assertEquals("Drinks", result.get(1).getName());
        assertTrue(result.get(1).isActive());

        verify(repo).findByActiveTrue();
        verifyNoMoreInteractions(repo);
    }
}
