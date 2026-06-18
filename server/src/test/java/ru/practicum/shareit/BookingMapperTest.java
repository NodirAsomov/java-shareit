package ru.practicum.shareit;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private BookingMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new BookingMapper();
    }

    @Test
    void shouldConvertBookingToDto() {
        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(10L);
        item.setName("Drill");

        Booking booking = new Booking();
        booking.setId(100L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.APPROVED);

        BookingDto dto = mapper.toDto(booking);

        assertEquals(100L, dto.getId());
        assertEquals(1L, dto.getBooker().getId());
        assertEquals(10L, dto.getItem().getId());
        assertEquals("Drill", dto.getItem().getName());
        assertEquals(BookingStatus.APPROVED, dto.getStatus());
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
    }

    @Test
    void shouldConvertCreateDtoToBooking() {
        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(10L);

        BookingCreateDto dto = new BookingCreateDto();
        dto.setStart(LocalDateTime.now());
        dto.setEnd(LocalDateTime.now().plusDays(2));

        Booking booking = mapper.toEntity(dto, user, item);

        assertEquals(user, booking.getBooker());
        assertEquals(item, booking.getItem());
        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }
}