package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    BookingServiceImpl service;

    User user;
    User owner;
    Item item;
    Booking booking;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        owner = new User();
        owner.setId(2L);

        item = new Item();
        item.setId(10L);
        item.setOwner(owner);
        item.setAvailable(true);

        booking = new Booking();
        booking.setId(100L);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
    }

    // ---------------- CREATE ----------------

    @Test
    void create_success() {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(10L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        BookingDto result = service.create(1L, dto);

        assertNotNull(result);
    }

    @Test
    void create_owner_conflict() {
        item.setOwner(user);

        BookingCreateDto dto = validDto();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(ConflictException.class,
                () -> service.create(1L, dto));
    }

    @Test
    void create_item_not_available() {
        item.setAvailable(false);

        BookingCreateDto dto = validDto();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class,
                () -> service.create(1L, dto));
    }

    @Test
    void create_invalid_dates() {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(10L);
        dto.setStart(LocalDateTime.now().plusDays(2));
        dto.setEnd(LocalDateTime.now().plusDays(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class,
                () -> service.create(1L, dto));
    }

    // ---------------- APPROVE ----------------

    @Test
    void approve_success() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        BookingDto result = service.approve(2L, 100L, true);

        assertNotNull(result);
    }

    @Test
    void approve_not_owner() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        assertThrows(AccessException.class,
                () -> service.approve(999L, 100L, true));
    }

    @Test
    void approve_already_processed() {
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        assertThrows(ConflictException.class,
                () -> service.approve(2L, 100L, true));
    }

    // ---------------- GET BY ID ----------------

    @Test
    void getById_success_owner() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        BookingDto result = service.getById(2L, 100L);

        assertNotNull(result);
    }

    @Test
    void getById_success_booker() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        BookingDto result = service.getById(1L, 100L);

        assertNotNull(result);
    }

    @Test
    void getById_access_denied() {
        User other = new User();
        other.setId(999L);

        booking.setBooker(user);
        booking.getItem().setOwner(owner);

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        assertThrows(AccessException.class,
                () -> service.getById(999L, 100L));
    }

    // ---------------- USER BOOKINGS SWITCH ----------------

    @Test
    void getUserBookings_switch_full() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(bookingRepository.findByBookerIdOrderByStartDesc(1L))
                .thenReturn(List.of());
        service.getUserBookings(1L, BookingState.ALL);

        when(bookingRepository.findCurrentByBooker(1L))
                .thenReturn(List.of());
        service.getUserBookings(1L, BookingState.CURRENT);

        when(bookingRepository.findPastByBooker(1L))
                .thenReturn(List.of());
        service.getUserBookings(1L, BookingState.PAST);

        when(bookingRepository.findFutureByBooker(1L))
                .thenReturn(List.of());
        service.getUserBookings(1L, BookingState.FUTURE);

        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(1L, BookingStatus.WAITING))
                .thenReturn(List.of());
        service.getUserBookings(1L, BookingState.WAITING);

        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(1L, BookingStatus.REJECTED))
                .thenReturn(List.of());
        service.getUserBookings(1L, BookingState.REJECTED);
    }

    // ---------------- OWNER BOOKINGS SWITCH ----------------

    @Test
    void getOwnerBookings_switch_full() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));

        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(2L))
                .thenReturn(List.of());
        service.getOwnerBookings(2L, BookingState.ALL);

        when(bookingRepository.findCurrentByOwner(2L))
                .thenReturn(List.of());
        service.getOwnerBookings(2L, BookingState.CURRENT);

        when(bookingRepository.findPastByOwner(2L))
                .thenReturn(List.of());
        service.getOwnerBookings(2L, BookingState.PAST);

        when(bookingRepository.findFutureByOwner(2L))
                .thenReturn(List.of());
        service.getOwnerBookings(2L, BookingState.FUTURE);

        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(2L, BookingStatus.WAITING))
                .thenReturn(List.of());
        service.getOwnerBookings(2L, BookingState.WAITING);

        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(2L, BookingStatus.REJECTED))
                .thenReturn(List.of());
        service.getOwnerBookings(2L, BookingState.REJECTED);
    }

    // ---------------- helper ----------------

    private BookingCreateDto validDto() {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(10L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        return dto;
    }
}