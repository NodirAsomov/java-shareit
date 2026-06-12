package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.BookingDto;
import ru.practicum.shareit.booking.*;
import ru.practicum.dto.BookingCreateDto;

import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.*;
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
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto create(Long userId, BookingCreateDto dto) {


        User user = getUser(userId);
        Item item = getItem(dto.getItemId());

        if (item.getOwner().getId().equals(userId)) {
            throw new ConflictException("Owner cannot book own item");
        }

        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available");
        }

        if (dto.getStart() == null
                || dto.getEnd() == null
                || !dto.getStart().isBefore(dto.getEnd())
                || dto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Invalid dates");
        }

        Booking booking = bookingMapper.toEntity(dto, user, item);

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approve(Long userId, Long bookingId, boolean approved) {

        Booking booking = getBooking(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessException("Only owner can approve");
        }

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ConflictException("Booking already processed");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {

        Booking booking = getBooking(bookingId);

        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);
        boolean isBooker = booking.getBooker().getId().equals(userId);

        if (!isOwner && !isBooker) {
            throw new AccessException("Access denied");
        }

        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, BookingState state) {

        getUser(userId);

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

        getUser(userId);
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

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id=" + userId));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id=" + itemId));
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id=" + bookingId));
    }
}