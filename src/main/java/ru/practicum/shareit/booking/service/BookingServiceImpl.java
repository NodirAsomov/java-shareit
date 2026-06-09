package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingMapper bookingMapper;


    @Override
    public BookingDto create(Long userId, BookingCreateDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getAvailable()) {
            throw new RuntimeException("Item is not available");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Owner cannot book own item");
        }

        if (dto.getStart() == null
                || dto.getEnd() == null
                || !dto.getStart().isBefore(dto.getEnd())
                || dto.getStart().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Invalid dates");
        }

        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setStatus(BookingStatus.WAITING);

        booking = bookingRepository.save(booking);

        return bookingMapper.toDto(booking);
    }


    @Override
    public BookingDto approve(Long userId, Long bookingId, boolean approved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new RuntimeException("Only owner can approve");
        }

        booking.setStatus(
                approved ? BookingStatus.APPROVED : BookingStatus.REJECTED
        );

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override

    public BookingDto getById(Long userId, Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);
        boolean isBooker = booking.getBooker().getId().equals(userId);

        if (!isOwner && !isBooker) {
            throw new RuntimeException("Access denied");
        }

        return bookingMapper.toDto(booking);
    }


    @Override
    public List<BookingDto> getUserBookings(Long userId, BookingState state) {

        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentByBooker(userId);
                break;
            case PAST:
                bookings = bookingRepository.findPastByBooker(userId);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureByBooker(userId);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = List.of();
        }

        return bookings.stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override

    public List<BookingDto> getOwnerBookings(Long userId, BookingState state) {

        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentByOwner(userId);
                break;
            case PAST:
                bookings = bookingRepository.findPastByOwner(userId);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureByOwner(userId);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = List.of();
        }

        return bookings.stream()
                .map(bookingMapper::toDto)
                .toList();
    }
}
