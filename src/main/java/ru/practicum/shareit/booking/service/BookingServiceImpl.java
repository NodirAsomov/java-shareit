package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public BookingDto create(Long userId, BookingDto dto) {

        User booker = userStorage.getById(userId);
        Item item = itemStorage.getById(dto.getItemId());

        if (!item.getAvailable()) {
            throw new RuntimeException("Item is not available");
        }

        Booking booking = BookingMapper.fromDto(dto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        return BookingMapper.toDto(bookingStorage.add(booking));
    }

    @Override
    public BookingDto approve(Long ownerId, Long bookingId, Boolean approved) {

        Booking booking = bookingStorage.getById(bookingId);

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Only owner can approve booking");
        }

        booking.setStatus(
                approved ? BookingStatus.APPROVED : BookingStatus.REJECTED
        );

        return BookingMapper.toDto(bookingStorage.update(booking));
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {

        Booking booking = bookingStorage.getById(bookingId);

        if (!booking.getBooker().getId().equals(userId)
                && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getAll() {
        return bookingStorage.getAll()
                .stream()
                .map(BookingMapper::toDto)
                .toList();
    }
}