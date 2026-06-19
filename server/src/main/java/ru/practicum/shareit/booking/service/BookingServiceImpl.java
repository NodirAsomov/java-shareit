package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto create(Long userId, BookingCreateDto dto) {

        User user = getUser(userId);
        Item item = getItem(dto.getItemId());

        if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {
            throw new ConflictException("Owner cannot book own item");
        }

        if (!Boolean.TRUE.equals(item.getAvailable())) {
            throw new ValidationException("Item is not available");
        }

        if (dto.getStart() == null
                || dto.getEnd() == null
                || !dto.getStart().isBefore(dto.getEnd())
                || dto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Invalid dates");
        }

        Booking booking = BookingMapper.toEntity(dto, user, item);

        // status уже выставлен в mapper → НЕ ДУБЛИРУЕМ

        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approve(Long userId, Long bookingId, boolean approved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (booking.getItem() == null
                || booking.getItem().getOwner() == null
                || !booking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessException("Only owner can approve");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ConflictException("Already processed");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        boolean isOwner = booking.getItem() != null
                && booking.getItem().getOwner() != null
                && booking.getItem().getOwner().getId().equals(userId);

        boolean isBooker = booking.getBooker() != null
                && booking.getBooker().getId().equals(userId);

        if (!isOwner && !isBooker) {
            throw new AccessException("Access denied");
        }

        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, BookingState state) {

        getUser(userId);

        if (state == null) {
            state = BookingState.ALL;
        }

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findCurrentByBooker(userId);
            case PAST -> bookingRepository.findPastByBooker(userId);
            case FUTURE -> bookingRepository.findFutureByBooker(userId);
            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        };

        return bookings.stream()
                .map(BookingMapper::toDto)
                .toList();
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long userId, BookingState state) {

        getUser(userId);

        if (state == null) {
            state = BookingState.ALL;
        }

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findCurrentByOwner(userId);
            case PAST -> bookingRepository.findPastByOwner(userId);
            case FUTURE -> bookingRepository.findFutureByOwner(userId);
            case WAITING -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        };

        return bookings.stream()
                .map(BookingMapper::toDto)
                .toList();
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id=" + userId));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id=" + itemId));
    }
}