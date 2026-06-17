package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
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

    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingMapper bookingMapper;

    @InjectMocks
    BookingServiceImpl service;


    @Test
    void create_success() {
        User user = new User();
        user.setId(1L);

        User owner = new User();
        owner.setId(2L);

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

        BookingDto response = service.create(1L, dto);

        assertNotNull(response);
    }

    @Test
    void create_ownerCannotBook() {
        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(10L);
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
        User user = new User();
        user.setId(1L);

        User owner = new User();
        owner.setId(2L);

        Item item = new Item();
        item.setId(10L);
        item.setAvailable(true);
        item.setOwner(owner);

        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(10L);
        dto.setStart(LocalDateTime.now().plusDays(2));
        dto.setEnd(LocalDateTime.now().plusDays(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class,
                () -> service.create(1L, dto));
    }


    @Test
    void approve_success() {
        User owner = new User();
        owner.setId(1L);

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
        User owner = new User();
        owner.setId(2L);

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
        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(5L)).thenReturn(Optional.of(booking));

        assertThrows(ConflictException.class,
                () -> service.approve(1L, 5L, true));
    }


    @Test
    void getById_accessDenied() {
        User owner = new User();
        owner.setId(2L);
        User booker = new User();
        booker.setId(3L);

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
    void userBookings_safe() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdOrderByStartDesc(1L)).thenReturn(List.of());

        service.getUserBookings(1L, BookingState.ALL);
    }

    @Test
    void ownerBookings_safe() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(1L))
                .thenReturn(List.of());

        service.getOwnerBookings(1L, BookingState.ALL);
    }
}