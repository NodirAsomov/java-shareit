package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

public class BookingMapper {


    public static BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(
                        booking.getItem() != null
                                ? booking.getItem().getId()
                                : null
                )
                .bookerId(
                        booking.getBooker() != null
                                ? booking.getBooker().getId()
                                : null
                )
                .status(
                        booking.getStatus() != null
                                ? booking.getStatus().name()
                                : null
                )
                .build();
    }


    public static Booking fromDto(BookingDto dto) {
        return Booking.builder()
                .id(dto.getId())
                .start(dto.getStart())
                .end(dto.getEnd())
                .build();
    }
}
