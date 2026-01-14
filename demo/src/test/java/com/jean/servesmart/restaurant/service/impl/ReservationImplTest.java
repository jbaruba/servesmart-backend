package com.jean.servesmart.restaurant.service.impl;

import com.jean.servesmart.restaurant.dto.reservation.ReservationCreateDto;
import com.jean.servesmart.restaurant.dto.reservation.ReservationResponseDto;
import com.jean.servesmart.restaurant.dto.reservation.ReservationUpdateDto;
import com.jean.servesmart.restaurant.exception.reservation.ReservationInvalidDataException;
import com.jean.servesmart.restaurant.exception.reservation.ReservationNotFoundException;
import com.jean.servesmart.restaurant.exception.reservation.ReservationStatusNotFoundException;
import com.jean.servesmart.restaurant.exception.reservation.ReservationTableNotFoundException;
import com.jean.servesmart.restaurant.exception.reservation.ReservationTimeSlotUnavailableException;
import com.jean.servesmart.restaurant.model.Reservation;
import com.jean.servesmart.restaurant.model.ReservationStatus;
import com.jean.servesmart.restaurant.model.RestaurantTable;
import com.jean.servesmart.restaurant.repository.ReservationRepository;
import com.jean.servesmart.restaurant.repository.ReservationStatusRepository;
import com.jean.servesmart.restaurant.repository.RestaurantTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationImplTest {

    @Mock
    private ReservationRepository reservationRepo;

    @Mock
    private RestaurantTableRepository tableRepo;

    @Mock
    private ReservationStatusRepository statusRepo;

    private ReservationImpl service;

    @BeforeEach
    void setup() {
        service = new ReservationImpl(reservationRepo, tableRepo, statusRepo);
    }

    @Test
    void create_whenDtoNull_throwsInvalidData() {
        assertThrows(ReservationInvalidDataException.class, () -> service.create(null));
        verifyNoInteractions(reservationRepo, tableRepo, statusRepo);
    }

    @Test
    void create_whenTableIdNull_throwsInvalidData() {
        ReservationCreateDto dto = new ReservationCreateDto();
        dto.setRestaurantTableId(null);
        dto.setFullName("A");
        dto.setPartySize(2);
        dto.setEventDateTime(LocalDateTime.now());

        assertThrows(ReservationInvalidDataException.class, () -> service.create(dto));
        verifyNoInteractions(reservationRepo, tableRepo, statusRepo);
    }

    @Test
    void create_whenFullNameBlank_throwsInvalidData() {
        ReservationCreateDto dto = new ReservationCreateDto();
        dto.setRestaurantTableId(1);
        dto.setFullName(" ");
        dto.setPartySize(2);
        dto.setEventDateTime(LocalDateTime.now());

        assertThrows(ReservationInvalidDataException.class, () -> service.create(dto));
        verifyNoInteractions(reservationRepo, tableRepo, statusRepo);
    }

    @Test
    void create_whenPartySizeInvalid_throwsInvalidData() {
        ReservationCreateDto dto = new ReservationCreateDto();
        dto.setRestaurantTableId(1);
        dto.setFullName("A");
        dto.setPartySize(0);
        dto.setEventDateTime(LocalDateTime.now());

        assertThrows(ReservationInvalidDataException.class, () -> service.create(dto));
        verifyNoInteractions(reservationRepo, tableRepo, statusRepo);
    }

    @Test
    void create_whenEventDateTimeNull_throwsInvalidData() {
        ReservationCreateDto dto = new ReservationCreateDto();
        dto.setRestaurantTableId(1);
        dto.setFullName("A");
        dto.setPartySize(2);
        dto.setEventDateTime(null);

        assertThrows(ReservationInvalidDataException.class, () -> service.create(dto));
        verifyNoInteractions(reservationRepo, tableRepo, statusRepo);
    }

    @Test
    void create_whenTableNotFound_throwsTableNotFound() {
        ReservationCreateDto dto = new ReservationCreateDto();
        dto.setRestaurantTableId(1);
        dto.setFullName("A");
        dto.setPartySize(2);
        dto.setEventDateTime(LocalDateTime.of(2030, 1, 1, 12, 0));

        when(tableRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(ReservationTableNotFoundException.class, () -> service.create(dto));

        verify(tableRepo).findById(1);
        verifyNoInteractions(reservationRepo, statusRepo);
    }

    @Test
    void create_whenTimeslotUnavailable_throwsTimeSlotUnavailable() {
        LocalDateTime event = LocalDateTime.of(2030, 1, 1, 12, 0);

        RestaurantTable table = new RestaurantTable();
        table.setId(1);
        table.setLabel("T1");

        ReservationCreateDto dto = new ReservationCreateDto();
        dto.setRestaurantTableId(1);
        dto.setFullName("A");
        dto.setPartySize(2);
        dto.setEventDateTime(event);

        when(tableRepo.findById(1)).thenReturn(Optional.of(table));
        when(reservationRepo.existsByRestaurantTable_IdAndEventDateTime(1, event)).thenReturn(true);

        assertThrows(ReservationTimeSlotUnavailableException.class, () -> service.create(dto));

        verify(tableRepo).findById(1);
        verify(reservationRepo).existsByRestaurantTable_IdAndEventDateTime(1, event);
        verifyNoInteractions(statusRepo);
    }

    @Test
    void create_whenStatusNull_usesDefaultPending_andIfNotFound_throwsStatusNotFound() {
        LocalDateTime event = LocalDateTime.of(2030, 1, 1, 12, 0);

        RestaurantTable table = new RestaurantTable();
        table.setId(1);
        table.setLabel("T1");

        ReservationCreateDto dto = new ReservationCreateDto();
        dto.setRestaurantTableId(1);
        dto.setFullName("A");
        dto.setPartySize(2);
        dto.setEventDateTime(event);
        dto.setStatusName(null);

        when(tableRepo.findById(1)).thenReturn(Optional.of(table));
        when(reservationRepo.existsByRestaurantTable_IdAndEventDateTime(1, event)).thenReturn(false);
        when(statusRepo.findByName("PENDING")).thenReturn(Optional.empty());

        assertThrows(ReservationStatusNotFoundException.class, () -> service.create(dto));

        verify(statusRepo).findByName("PENDING");
    }

    @Test
    void create_whenStatusBlank_usesDefaultPending() {
        LocalDateTime event = LocalDateTime.of(2030, 1, 1, 12, 0);

        RestaurantTable table = new RestaurantTable();
        table.setId(1);
        table.setLabel("T1");

        ReservationStatus pending = new ReservationStatus();
        pending.setName("PENDING");

        ReservationCreateDto dto = new ReservationCreateDto();
        dto.setRestaurantTableId(1);
        dto.setFullName("  Alex  ");
        dto.setPartySize(2);
        dto.setPhoneNumber("06");
        dto.setEventDateTime(event);
        dto.setStatusName("   ");

        Reservation saved = new Reservation();
        saved.setId(10);
        saved.setRestaurantTable(table);
        saved.setFullName("Alex");
        saved.setPartySize(2);
        saved.setPhoneNumber("06");
        saved.setEventDateTime(event);
        saved.setStatus(pending);

        when(tableRepo.findById(1)).thenReturn(Optional.of(table));
        when(reservationRepo.existsByRestaurantTable_IdAndEventDateTime(1, event)).thenReturn(false);
        when(statusRepo.findByName("PENDING")).thenReturn(Optional.of(pending));
        when(reservationRepo.save(any(Reservation.class))).thenReturn(saved);

        ReservationResponseDto result = service.create(dto);

        assertEquals(10, result.getId());
        assertEquals(1, result.getRestaurantTableId());
        assertEquals("T1", result.getRestaurantTableLabel());
        assertEquals("Alex", result.getFullName());
        assertEquals(2, result.getPartySize());
        assertEquals("06", result.getPhoneNumber());
        assertEquals(event, result.getEventDateTime());
        assertEquals("PENDING", result.getStatusName());

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepo).save(captor.capture());
        assertEquals("Alex", captor.getValue().getFullName());
        assertEquals(table, captor.getValue().getRestaurantTable());
        assertEquals(pending, captor.getValue().getStatus());
    }

    @Test
    void create_whenExplicitStatusNotFound_throwsStatusNotFound() {
        LocalDateTime event = LocalDateTime.of(2030, 1, 1, 12, 0);

        RestaurantTable table = new RestaurantTable();
        table.setId(1);
        table.setLabel("T1");

        ReservationCreateDto dto = new ReservationCreateDto();
        dto.setRestaurantTableId(1);
        dto.setFullName("A");
        dto.setPartySize(2);
        dto.setEventDateTime(event);
        dto.setStatusName("CONFIRMED");

        when(tableRepo.findById(1)).thenReturn(Optional.of(table));
        when(reservationRepo.existsByRestaurantTable_IdAndEventDateTime(1, event)).thenReturn(false);
        when(statusRepo.findByName("CONFIRMED")).thenReturn(Optional.empty());

        assertThrows(ReservationStatusNotFoundException.class, () -> service.create(dto));

        verify(statusRepo).findByName("CONFIRMED");
    }

    @Test
    void create_whenValidWithExplicitStatus_savesAndMaps() {
        LocalDateTime event = LocalDateTime.of(2030, 1, 1, 12, 0);

        RestaurantTable table = new RestaurantTable();
        table.setId(1);
        table.setLabel("T1");

        ReservationStatus confirmed = new ReservationStatus();
        confirmed.setName("CONFIRMED");

        ReservationCreateDto dto = new ReservationCreateDto();
        dto.setRestaurantTableId(1);
        dto.setFullName("  Alex  ");
        dto.setPartySize(2);
        dto.setPhoneNumber("06");
        dto.setEventDateTime(event);
        dto.setStatusName("CONFIRMED");

        Reservation saved = new Reservation();
        saved.setId(10);
        saved.setRestaurantTable(table);
        saved.setFullName("Alex");
        saved.setPartySize(2);
        saved.setPhoneNumber("06");
        saved.setEventDateTime(event);
        saved.setStatus(confirmed);

        when(tableRepo.findById(1)).thenReturn(Optional.of(table));
        when(reservationRepo.existsByRestaurantTable_IdAndEventDateTime(1, event)).thenReturn(false);
        when(statusRepo.findByName("CONFIRMED")).thenReturn(Optional.of(confirmed));
        when(reservationRepo.save(any(Reservation.class))).thenReturn(saved);

        ReservationResponseDto result = service.create(dto);

        assertEquals(10, result.getId());
        assertEquals("Alex", result.getFullName());
        assertEquals("CONFIRMED", result.getStatusName());

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepo).save(captor.capture());
        assertEquals("Alex", captor.getValue().getFullName());
        assertEquals(confirmed, captor.getValue().getStatus());
    }

    @Test
    void getById_whenIdNull_throwsInvalidData() {
        assertThrows(ReservationInvalidDataException.class, () -> service.getById(null));
        verifyNoInteractions(reservationRepo, tableRepo, statusRepo);
    }

    @Test
    void getById_whenNotFound_returnsEmpty() {
        when(reservationRepo.findById(1)).thenReturn(Optional.empty());

        Optional<ReservationResponseDto> result = service.getById(1);

        assertTrue(result.isEmpty());
        verify(reservationRepo).findById(1);
    }

    @Test
    void getByStatus_whenBlank_throwsInvalidData() {
        assertThrows(ReservationInvalidDataException.class, () -> service.getByStatus(" "));
        verifyNoInteractions(reservationRepo, tableRepo, statusRepo);
    }

    @Test
    void getByStatus_mapsReservations() {
        ReservationStatus status = new ReservationStatus();
        status.setName("PENDING");

        RestaurantTable table = new RestaurantTable();
        table.setId(1);
        table.setLabel("T1");

        Reservation r = new Reservation();
        r.setId(10);
        r.setRestaurantTable(table);
        r.setFullName("Alex");
        r.setPartySize(2);
        r.setEventDateTime(LocalDateTime.of(2030, 1, 1, 12, 0));
        r.setStatus(status);

        when(reservationRepo.findByStatus_Name("PENDING")).thenReturn(List.of(r));

        List<ReservationResponseDto> result = service.getByStatus("PENDING");

        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getId());
        assertEquals("T1", result.get(0).getRestaurantTableLabel());
        assertEquals("PENDING", result.get(0).getStatusName());
        verify(reservationRepo).findByStatus_Name("PENDING");
    }

    @Test
    void getByTableAndDateRange_whenAnyNull_throwsInvalidData() {
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2030, 1, 2, 0, 0);

        assertThrows(ReservationInvalidDataException.class, () -> service.getByTableAndDateRange(null, start, end));
        assertThrows(ReservationInvalidDataException.class, () -> service.getByTableAndDateRange(1, null, end));
        assertThrows(ReservationInvalidDataException.class, () -> service.getByTableAndDateRange(1, start, null));

        verifyNoInteractions(reservationRepo, tableRepo, statusRepo);
    }

    @Test
    void getByTableAndDateRange_mapsReservations() {
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2030, 1, 2, 0, 0);

        ReservationStatus status = new ReservationStatus();
        status.setName("PENDING");

        RestaurantTable table = new RestaurantTable();
        table.setId(1);
        table.setLabel("T1");

        Reservation r = new Reservation();
        r.setId(10);
        r.setRestaurantTable(table);
        r.setFullName("Alex");
        r.setPartySize(2);
        r.setEventDateTime(LocalDateTime.of(2030, 1, 1, 12, 0));
        r.setStatus(status);

        when(reservationRepo.findByRestaurantTable_IdAndEventDateTimeBetween(1, start, end)).thenReturn(List.of(r));

        List<ReservationResponseDto> result = service.getByTableAndDateRange(1, start, end);

        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getId());
        assertEquals(1, result.get(0).getRestaurantTableId());
        verify(reservationRepo).findByRestaurantTable_IdAndEventDateTimeBetween(1, start, end);
    }

    @Test
    void update_whenInvalidInputs_throwsInvalidData() {
        ReservationUpdateDto dto = new ReservationUpdateDto();
        assertThrows(ReservationInvalidDataException.class, () -> service.update(null, dto));
        assertThrows(ReservationInvalidDataException.class, () -> service.update(1, null));
        verifyNoInteractions(reservationRepo, tableRepo, statusRepo);
    }

    @Test
    void update_whenReservationNotFound_throwsNotFound() {
        when(reservationRepo.findById(1)).thenReturn(Optional.empty());

        ReservationUpdateDto dto = new ReservationUpdateDto();

        assertThrows(ReservationNotFoundException.class, () -> service.update(1, dto));

        verify(reservationRepo).findById(1);
        verifyNoMoreInteractions(reservationRepo);
        verifyNoInteractions(tableRepo, statusRepo);
    }

    @Test
    void update_whenTableIdProvidedButNotFound_throwsTableNotFound() {
        Reservation r = new Reservation();
        r.setId(1);
        RestaurantTable current = new RestaurantTable();
        current.setId(1);
        current.setLabel("T1");
        r.setRestaurantTable(current);

        when(reservationRepo.findById(1)).thenReturn(Optional.of(r));
        when(tableRepo.findById(2)).thenReturn(Optional.empty());

        ReservationUpdateDto dto = new ReservationUpdateDto();
        dto.setRestaurantTableId(2);

        assertThrows(ReservationTableNotFoundException.class, () -> service.update(1, dto));

        verify(tableRepo).findById(2);
    }

    @Test
    void update_whenFullNameBlank_throwsInvalidData() {
        Reservation r = new Reservation();
        r.setId(1);
        RestaurantTable current = new RestaurantTable();
        current.setId(1);
        current.setLabel("T1");
        r.setRestaurantTable(current);

        when(reservationRepo.findById(1)).thenReturn(Optional.of(r));

        ReservationUpdateDto dto = new ReservationUpdateDto();
        dto.setFullName(" ");

        assertThrows(ReservationInvalidDataException.class, () -> service.update(1, dto));
    }

    @Test
    void update_whenPartySizeInvalid_throwsInvalidData() {
        Reservation r = new Reservation();
        r.setId(1);
        RestaurantTable current = new RestaurantTable();
        current.setId(1);
        current.setLabel("T1");
        r.setRestaurantTable(current);

        when(reservationRepo.findById(1)).thenReturn(Optional.of(r));

        ReservationUpdateDto dto = new ReservationUpdateDto();
        dto.setPartySize(0);

        assertThrows(ReservationInvalidDataException.class, () -> service.update(1, dto));
    }

    @Test
    void update_whenEventDateTimeConflicts_throwsTimeSlotUnavailable() {
        LocalDateTime newEvent = LocalDateTime.of(2030, 1, 1, 12, 0);

        RestaurantTable current = new RestaurantTable();
        current.setId(1);
        current.setLabel("T1");

        Reservation r = new Reservation();
        r.setId(1);
        r.setRestaurantTable(current);

        when(reservationRepo.findById(1)).thenReturn(Optional.of(r));
        when(reservationRepo.existsByRestaurantTable_IdAndEventDateTime(1, newEvent)).thenReturn(true);

        ReservationUpdateDto dto = new ReservationUpdateDto();
        dto.setEventDateTime(newEvent);

        assertThrows(ReservationTimeSlotUnavailableException.class, () -> service.update(1, dto));

        verify(reservationRepo).existsByRestaurantTable_IdAndEventDateTime(1, newEvent);
    }

    @Test
    void update_whenStatusBlank_throwsInvalidData() {
        RestaurantTable current = new RestaurantTable();
        current.setId(1);
        current.setLabel("T1");

        Reservation r = new Reservation();
        r.setId(1);
        r.setRestaurantTable(current);

        when(reservationRepo.findById(1)).thenReturn(Optional.of(r));

        ReservationUpdateDto dto = new ReservationUpdateDto();
        dto.setStatusName("   ");

        assertThrows(ReservationInvalidDataException.class, () -> service.update(1, dto));
    }

    @Test
    void update_whenStatusNotFound_throwsStatusNotFound() {
        RestaurantTable current = new RestaurantTable();
        current.setId(1);
        current.setLabel("T1");

        Reservation r = new Reservation();
        r.setId(1);
        r.setRestaurantTable(current);

        when(reservationRepo.findById(1)).thenReturn(Optional.of(r));
        when(statusRepo.findByName("CONFIRMED")).thenReturn(Optional.empty());

        ReservationUpdateDto dto = new ReservationUpdateDto();
        dto.setStatusName("CONFIRMED");

        assertThrows(ReservationStatusNotFoundException.class, () -> service.update(1, dto));

        verify(statusRepo).findByName("CONFIRMED");
    }

    @Test
    void update_whenValid_updatesAndSaves() {
        LocalDateTime newEvent = LocalDateTime.of(2030, 1, 1, 12, 0);

        RestaurantTable current = new RestaurantTable();
        current.setId(1);
        current.setLabel("T1");

        RestaurantTable newTable = new RestaurantTable();
        newTable.setId(2);
        newTable.setLabel("T2");

        ReservationStatus confirmed = new ReservationStatus();
        confirmed.setName("CONFIRMED");

        Reservation r = new Reservation();
        r.setId(1);
        r.setRestaurantTable(current);
        r.setFullName("Old");
        r.setPartySize(2);
        r.setPhoneNumber("00");
        r.setEventDateTime(LocalDateTime.of(2030, 1, 1, 10, 0));

        when(reservationRepo.findById(1)).thenReturn(Optional.of(r));
        when(tableRepo.findById(2)).thenReturn(Optional.of(newTable));
        when(reservationRepo.existsByRestaurantTable_IdAndEventDateTime(2, newEvent)).thenReturn(false);
        when(statusRepo.findByName("CONFIRMED")).thenReturn(Optional.of(confirmed));
        when(reservationRepo.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        ReservationUpdateDto dto = new ReservationUpdateDto();
        dto.setRestaurantTableId(2);
        dto.setFullName("  Alex  ");
        dto.setPartySize(4);
        dto.setPhoneNumber("06");
        dto.setEventDateTime(newEvent);
        dto.setStatusName("CONFIRMED");

        ReservationResponseDto result = service.update(1, dto);

        assertEquals(2, result.getRestaurantTableId());
        assertEquals("T2", result.getRestaurantTableLabel());
        assertEquals("Alex", result.getFullName());
        assertEquals(4, result.getPartySize());
        assertEquals("06", result.getPhoneNumber());
        assertEquals(newEvent, result.getEventDateTime());
        assertEquals("CONFIRMED", result.getStatusName());

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepo).save(captor.capture());
        assertEquals(newTable, captor.getValue().getRestaurantTable());
        assertEquals("Alex", captor.getValue().getFullName());
        assertEquals(4, captor.getValue().getPartySize());
        assertEquals("06", captor.getValue().getPhoneNumber());
        assertEquals(newEvent, captor.getValue().getEventDateTime());
        assertEquals(confirmed, captor.getValue().getStatus());
    }

    @Test
    void delete_whenIdNull_throwsInvalidData() {
        assertThrows(ReservationInvalidDataException.class, () -> service.delete(null));
        verifyNoInteractions(reservationRepo, tableRepo, statusRepo);
    }

    @Test
    void delete_whenNotExists_throwsNotFound() {
        when(reservationRepo.existsById(1)).thenReturn(false);

        assertThrows(ReservationNotFoundException.class, () -> service.delete(1));

        verify(reservationRepo).existsById(1);
        verifyNoMoreInteractions(reservationRepo);
        verifyNoInteractions(tableRepo, statusRepo);
    }

    @Test
    void delete_whenExists_deletesAndReturnsTrue() {
        when(reservationRepo.existsById(1)).thenReturn(true);

        boolean result = service.delete(1);

        assertTrue(result);

        // FIX: service calls existsById FIRST
        verify(reservationRepo).existsById(1);
        verify(reservationRepo).deleteById(1);

        verifyNoMoreInteractions(reservationRepo);
        verifyNoInteractions(tableRepo, statusRepo);
    }
}
