package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;


@Component
public class BookingMapper {

    public BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();

        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());

        dto.setBooker(
                new BookingDto.BookerDto(booking.getBooker().getId())
        );

        Item item = booking.getItem();
        dto.setItem(
                new BookingDto.ItemDto(item.getId(), item.getName())
        );

        return dto;
    }

    public Booking toEntity(BookingCreateDto dto, User user, Item item) {
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }
}