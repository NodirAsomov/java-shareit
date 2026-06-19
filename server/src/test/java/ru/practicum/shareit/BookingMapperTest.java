package ru.practicum.shareit;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;




class BookingMapperTest {

    @Test
    void toEntity_ok() {
        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(2L);

        BookingCreateDto dto = new BookingCreateDto();
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        Booking booking = BookingMapper.toEntity(dto, user, item);

        assertNotNull(booking);
        assertEquals(item, booking.getItem());
        assertEquals(user, booking.getBooker());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
        assertNotNull(booking.getCreated());
    }

    @Test
    void toDto_ok() {
        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(2L);

        Booking booking = new Booking();
        booking.setId(10L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(user);
        booking.setItem(item);

        BookingDto dto = BookingMapper.toDto(booking);

        assertEquals(10L, dto.getId());
        assertEquals(BookingStatus.APPROVED, dto.getStatus());
        assertNotNull(dto.getBooker());
        assertNotNull(dto.getItem());
    }

    @Test
    void toShortDto_ok() {
        User user = new User();
        user.setId(5L);

        Booking booking = new Booking();
        booking.setId(100L);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));

        BookingShortDto dto = BookingMapper.toShortDto(booking);

        assertNotNull(dto);
        assertEquals(100L, dto.getId());
        assertEquals(5L, dto.getBookerId());
    }

    @Test
    void toShortDto_null_booking() {
        assertNull(BookingMapper.toShortDto(null));
    }

    @Test
    void toShortDto_null_booker() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(null);

        assertNull(BookingMapper.toShortDto(booking));
    }
}