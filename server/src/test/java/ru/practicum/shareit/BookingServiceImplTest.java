package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock BookingRepository bookingRepository;
    @Mock ItemRepository itemRepository;
    @Mock UserRepository userRepository;
    @Mock BookingMapper bookingMapper;

    @InjectMocks BookingServiceImpl service;

    // ---------- CREATE ----------

    @Test
    void create_success() {
        User user = new User(); user.setId(1L);
        User owner = new User(); owner.setId(2L);

        Item item = new Item();
        item.setId(10L);
        item.setAvailable(true);
        item.setOwner(owner);

        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(10L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        Booking booking = new Booking();
        BookingDto result = new BookingDto();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(bookingMapper.toEntity(any(), any(), any())).thenReturn(booking);
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingMapper.toDto(any())).thenReturn(result);

        assertNotNull(service.create(1L, dto));
    }

    @Test
    void create_ownerCannotBook() {
        User owner = new User(); owner.setId(1L);

        Item item = new Item();
        item.setOwner(owner);
        item.setAvailable(true);

        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(10L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(ConflictException.class,
                () -> service.create(1L, dto));
    }

    @Test
    void create_invalidDates() {
        User user = new User(); user.setId(1L);
        User owner = new User(); owner.setId(2L);

        Item item = new Item();
        item.setOwner(owner);
        item.setAvailable(true);

        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(10L);
        dto.setStart(LocalDateTime.now().plusDays(2));
        dto.setEnd(LocalDateTime.now().plusDays(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class,
                () -> service.create(1L, dto));
    }

    // ---------- APPROVE ----------

    @Test
    void approve_success() {
        User owner = new User(); owner.setId(1L);

        Item item = new Item();
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(5L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingMapper.toDto(any())).thenReturn(new BookingDto());

        assertNotNull(service.approve(1L, 5L, true));
    }

    @Test
    void approve_notOwner() {
        User owner = new User(); owner.setId(2L);

        Item item = new Item();
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(5L)).thenReturn(Optional.of(booking));

        assertThrows(AccessException.class,
                () -> service.approve(1L, 5L, true));
    }

    @Test
    void approve_alreadyProcessed() {
        User owner = new User(); owner.setId(1L);

        Item item = new Item();
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(5L)).thenReturn(Optional.of(booking));

        assertThrows(ConflictException.class,
                () -> service.approve(1L, 5L, true));
    }

    // ---------- GET BY ID ----------

    @Test
    void getById_accessDenied() {
        User owner = new User(); owner.setId(2L);
        User booker = new User(); booker.setId(3L);

        Item item = new Item();
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(7L)).thenReturn(Optional.of(booking));

        assertThrows(AccessException.class,
                () -> service.getById(1L, 7L));
    }

    @Test
    void getById_success_owner() {
        User owner = new User(); owner.setId(1L);

        Item item = new Item();
        item.setOwner(owner);

        User booker = new User(); booker.setId(2L);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(7L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(any())).thenReturn(new BookingDto());

        assertNotNull(service.getById(1L, 7L));
    }

    // ---------- USER BOOKINGS (ALL STATES) ----------

    @Test
    void userBookings_all() {
        User user = new User(); user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdOrderByStartDesc(1L)).thenReturn(List.of());

        assertNotNull(service.getUserBookings(1L, BookingState.ALL));
    }

    @Test
    void userBookings_current() {
        User user = new User(); user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findCurrentByBooker(1L)).thenReturn(List.of());

        assertNotNull(service.getUserBookings(1L, BookingState.CURRENT));
    }

    @Test
    void userBookings_past() {
        User user = new User(); user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findPastByBooker(1L)).thenReturn(List.of());

        assertNotNull(service.getUserBookings(1L, BookingState.PAST));
    }

    @Test
    void userBookings_future() {
        User user = new User(); user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findFutureByBooker(1L)).thenReturn(List.of());

        assertNotNull(service.getUserBookings(1L, BookingState.FUTURE));
    }

    @Test
    void userBookings_waiting() {
        User user = new User(); user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                1L, BookingStatus.WAITING)).thenReturn(List.of());

        assertNotNull(service.getUserBookings(1L, BookingState.WAITING));
    }

    @Test
    void userBookings_rejected() {
        User user = new User(); user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                1L, BookingStatus.REJECTED)).thenReturn(List.of());

        assertNotNull(service.getUserBookings(1L, BookingState.REJECTED));
    }

    // ---------- OWNER BOOKINGS ----------

    @Test
    void ownerBookings_all() {
        User user = new User(); user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(1L)).thenReturn(List.of());

        assertNotNull(service.getOwnerBookings(1L, BookingState.ALL));
    }

    @Test
    void ownerBookings_current() {
        User user = new User(); user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findCurrentByOwner(1L)).thenReturn(List.of());

        assertNotNull(service.getOwnerBookings(1L, BookingState.CURRENT));
    }

    @Test
    void ownerBookings_past() {
        User user = new User(); user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findPastByOwner(1L)).thenReturn(List.of());

        assertNotNull(service.getOwnerBookings(1L, BookingState.PAST));
    }

    @Test
    void ownerBookings_future() {
        User user = new User(); user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findFutureByOwner(1L)).thenReturn(List.of());

        assertNotNull(service.getOwnerBookings(1L, BookingState.FUTURE));
    }

    @Test
    void ownerBookings_waiting() {
        User user = new User(); user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                1L, BookingStatus.WAITING)).thenReturn(List.of());

        assertNotNull(service.getOwnerBookings(1L, BookingState.WAITING));
    }

    @Test
    void ownerBookings_rejected() {
        User user = new User(); user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                1L, BookingStatus.REJECTED)).thenReturn(List.of());

        assertNotNull(service.getOwnerBookings(1L, BookingState.REJECTED));
    }
}