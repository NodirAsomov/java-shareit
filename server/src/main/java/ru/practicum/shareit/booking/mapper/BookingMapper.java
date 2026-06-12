package ru.practicum.shareit.booking.mapper;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.dto.BookingCreateDto;
import ru.practicum.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Component
public class BookingMapper {

    public BookingDto toDto(Booking booking) {
        if (booking == null) return null;

        BookingDto dto = new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus().name(),
                booking.getItem().getId(),
                booking.getBooker().getId()
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