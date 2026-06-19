package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

public class BookingMapper {

    public static Booking toEntity(BookingCreateDto dto, User booker, Item item) {
        Booking booking = new Booking();

        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());

        booking.setStatus(BookingStatus.WAITING);
        booking.setCreated(LocalDateTime.now());

        return booking;
    }

    public static BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(UserMapper.toDto(booking.getBooker()))
                .item(ItemMapper.toDto(booking.getItem()))
                .build();
    }

    // ⭐ ВОТ ЭТО НУЖНО
    public static BookingShortDto toShortDto(Booking b) {
        if (b == null || b.getBooker() == null) return null;

        return BookingShortDto.builder()
                .id(b.getId())
                .bookerId(b.getBooker().getId())
                .start(b.getStart())
                .end(b.getEnd())
                .build();
    }
}