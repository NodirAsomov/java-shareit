package ru.practicum.shareit;


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

    private final BookingMapper mapper = new BookingMapper();

    @Test
    void toDto_fullMapping() {
        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(2L);
        item.setName("Item");

        Booking booking = new Booking();
        booking.setId(10L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(user);
        booking.setItem(item);

        BookingDto dto = mapper.toDto(booking);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals(BookingStatus.APPROVED, dto.getStatus());
        assertEquals(1L, dto.getBooker().getId());
        assertEquals(2L, dto.getItem().getId());
        assertEquals("Item", dto.getItem().getName());
    }

    @Test
    void toEntity_fullMapping() {
        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(2L);
        item.setName("Item");

        BookingCreateDto dto = new BookingCreateDto();
        dto.setStart(LocalDateTime.now());
        dto.setEnd(LocalDateTime.now().plusDays(1));

        Booking booking = mapper.toEntity(dto, user, item);

        assertNotNull(booking);
        assertEquals(user, booking.getBooker());
        assertEquals(item, booking.getItem());
        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void toDto_nullNestedObjects_shouldThrow() {
        Booking booking = new Booking();
        booking.setId(1L);


        Item item = new Item();
        item.setId(1L);
        item.setName("Item");

        booking.setItem(item);

        assertThrows(NullPointerException.class, () -> mapper.toDto(booking));
    }
}