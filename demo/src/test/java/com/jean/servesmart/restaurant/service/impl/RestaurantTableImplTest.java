package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.restauranttable.RestaurantTableCreateDto;
import com.jean.servesmart.restaurant.dto.restauranttable.RestaurantTableResponseDto;
import com.jean.servesmart.restaurant.dto.restauranttable.RestaurantTableUpdateDto;
import com.jean.servesmart.restaurant.exception.restauranttable.RestaurantTableInvalidDataException;
import com.jean.servesmart.restaurant.exception.restauranttable.RestaurantTableLabelAlreadyExistsException;
import com.jean.servesmart.restaurant.exception.restauranttable.RestaurantTableNotFoundException;
import com.jean.servesmart.restaurant.exception.restauranttable.RestaurantTableStatusNotFoundException;
import com.jean.servesmart.restaurant.model.RestaurantTable;
import com.jean.servesmart.restaurant.model.RestaurantTableStatus;
import com.jean.servesmart.restaurant.repository.RestaurantTableRepository;
import com.jean.servesmart.restaurant.repository.RestaurantTableStatusRepository;
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
class RestaurantTableImplTest {

    @Mock
    private RestaurantTableRepository repo;

    @Mock
    private RestaurantTableStatusRepository statusRepo;

    private RestaurantTableImpl service;

    @BeforeEach
    void setup() {
        service = new RestaurantTableImpl(repo, statusRepo);
    }

    @Test
    void create_whenDtoNull_throwsInvalidData() {
        assertThrows(RestaurantTableInvalidDataException.class, () -> service.create(null));
        verifyNoInteractions(repo, statusRepo);
    }

    @Test
    void create_whenLabelBlank_throwsInvalidData() {
        RestaurantTableCreateDto dto = new RestaurantTableCreateDto();
        dto.setLabel(" ");
        dto.setSeats(4);
        dto.setStatusName("FREE");

        assertThrows(RestaurantTableInvalidDataException.class, () -> service.create(dto));
    }

    @Test
    void create_whenSeatsInvalid_throwsInvalidData() {
        RestaurantTableCreateDto dto = new RestaurantTableCreateDto();
        dto.setLabel("T1");
        dto.setSeats(0);
        dto.setStatusName("FREE");

        assertThrows(RestaurantTableInvalidDataException.class, () -> service.create(dto));
    }

    @Test
    void create_whenLabelExists_throwsAlreadyExists() {
        RestaurantTableCreateDto dto = new RestaurantTableCreateDto();
        dto.setLabel("T1");
        dto.setSeats(4);
        dto.setStatusName("FREE");

        when(repo.findByLabel("T1")).thenReturn(Optional.of(new RestaurantTable()));

        assertThrows(RestaurantTableLabelAlreadyExistsException.class, () -> service.create(dto));

        verify(repo).findByLabel("T1");
    }

    @Test
    void create_whenStatusNotFound_throwsStatusNotFound() {
        RestaurantTableCreateDto dto = new RestaurantTableCreateDto();
        dto.setLabel("T1");
        dto.setSeats(4);
        dto.setStatusName("FREE");

        when(repo.findByLabel("T1")).thenReturn(Optional.empty());
        when(statusRepo.findByName("FREE")).thenReturn(Optional.empty());

        assertThrows(RestaurantTableStatusNotFoundException.class, () -> service.create(dto));

        verify(statusRepo).findByName("FREE");
    }

    @Test
    void create_whenValid_savesAndReturnsDto() {
        RestaurantTableCreateDto dto = new RestaurantTableCreateDto();
        dto.setLabel(" T1 ");
        dto.setSeats(4);
        dto.setStatusName("FREE");
        dto.setActive(true);

        RestaurantTableStatus status = new RestaurantTableStatus();
        status.setName("FREE");

        when(repo.findByLabel("T1")).thenReturn(Optional.empty());
        when(statusRepo.findByName("FREE")).thenReturn(Optional.of(status));

        RestaurantTable saved = new RestaurantTable();
        saved.setId(1);
        saved.setLabel("T1");
        saved.setSeats(4);
        saved.setActive(true);
        saved.setStatus(status);

        when(repo.save(any(RestaurantTable.class))).thenReturn(saved);

        RestaurantTableResponseDto result = service.create(dto);

        assertEquals(1, result.getId());
        assertEquals("T1", result.getLabel());
        assertEquals(4, result.getSeats());
        assertEquals("FREE", result.getStatusName());
        assertTrue(result.isActive());

        ArgumentCaptor<RestaurantTable> captor = ArgumentCaptor.forClass(RestaurantTable.class);
        verify(repo).save(captor.capture());
        assertEquals("T1", captor.getValue().getLabel());
    }

    @Test
    void getById_whenIdNull_throwsInvalidData() {
        assertThrows(RestaurantTableInvalidDataException.class, () -> service.getById(null));
    }

    @Test
    void getById_whenNotFound_returnsEmpty() {
        when(repo.findById(1)).thenReturn(Optional.empty());

        Optional<RestaurantTableResponseDto> result = service.getById(1);

        assertTrue(result.isEmpty());
        verify(repo).findById(1);
    }

    @Test
    void getAll_mapsAll() {
        RestaurantTable t = new RestaurantTable();
        t.setId(1);
        t.setLabel("T1");
        t.setSeats(4);
        t.setActive(true);

        when(repo.findAll()).thenReturn(List.of(t));

        List<RestaurantTableResponseDto> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals("T1", result.get(0).getLabel());
        verify(repo).findAll();
    }

    @Test
    void getActive_mapsOnlyActive() {
        RestaurantTable t = new RestaurantTable();
        t.setId(1);
        t.setLabel("T1");
        t.setSeats(4);
        t.setActive(true);

        when(repo.findByActiveTrue()).thenReturn(List.of(t));

        List<RestaurantTableResponseDto> result = service.getActive();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
        verify(repo).findByActiveTrue();
    }

    @Test
    void getByStatus_whenBlank_throwsInvalidData() {
        assertThrows(RestaurantTableInvalidDataException.class, () -> service.getByStatus(" "));
    }

    @Test
    void update_whenNotFound_throwsNotFound() {
        when(repo.findById(1)).thenReturn(Optional.empty());

        RestaurantTableUpdateDto dto = new RestaurantTableUpdateDto();

        assertThrows(RestaurantTableNotFoundException.class, () -> service.update(1, dto));
    }

    @Test
    void update_whenLabelExists_throwsAlreadyExists() {
        RestaurantTable t = new RestaurantTable();
        t.setId(1);
        t.setLabel("T1");

        when(repo.findById(1)).thenReturn(Optional.of(t));
        when(repo.findByLabel("T2")).thenReturn(Optional.of(new RestaurantTable()));

        RestaurantTableUpdateDto dto = new RestaurantTableUpdateDto();
        dto.setLabel("T2");

        assertThrows(RestaurantTableLabelAlreadyExistsException.class, () -> service.update(1, dto));
    }

    @Test
    void update_whenValid_updatesAndSaves() {
        RestaurantTableStatus status = new RestaurantTableStatus();
        status.setName("BUSY");

        RestaurantTable t = new RestaurantTable();
        t.setId(1);
        t.setLabel("T1");
        t.setSeats(4);
        t.setActive(true);

        when(repo.findById(1)).thenReturn(Optional.of(t));
        when(statusRepo.findByName("BUSY")).thenReturn(Optional.of(status));
        when(repo.save(any(RestaurantTable.class))).thenAnswer(inv -> inv.getArgument(0));

        RestaurantTableUpdateDto dto = new RestaurantTableUpdateDto();
        dto.setLabel("T2");
        dto.setSeats(6);
        dto.setStatusName("BUSY");
        dto.setActive(false);

        RestaurantTableResponseDto result = service.update(1, dto);

        assertEquals("T2", result.getLabel());
        assertEquals(6, result.getSeats());
        assertEquals("BUSY", result.getStatusName());
        assertFalse(result.isActive());

        verify(repo).save(any(RestaurantTable.class));
    }

    @Test
    void delete_whenIdNull_throwsInvalidData() {
        assertThrows(RestaurantTableInvalidDataException.class, () -> service.delete(null));
    }

    @Test
    void delete_whenNotExists_throwsNotFound() {
        when(repo.existsById(1)).thenReturn(false);

        assertThrows(RestaurantTableNotFoundException.class, () -> service.delete(1));
    }

    @Test
    void delete_whenExists_deletesAndReturnsTrue() {
        when(repo.existsById(1)).thenReturn(true);

        boolean result = service.delete(1);

        assertTrue(result);
        verify(repo).deleteById(1);
    }
}
