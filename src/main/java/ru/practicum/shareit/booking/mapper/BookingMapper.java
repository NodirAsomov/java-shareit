package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;


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
}