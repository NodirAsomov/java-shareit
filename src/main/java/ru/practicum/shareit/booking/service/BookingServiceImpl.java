package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto create(Long userId, BookingCreateDto dto) {
        return null;
    }

    @Override
    public BookingDto approve(Long userId, Long bookingId, boolean approved) {
        return null;
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        return null;
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, BookingState state) {
        return null;
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long userId, BookingState state) {
        return null;
    }
}
