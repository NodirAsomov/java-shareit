package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    private List<CommentDto> getComments(Long itemId) {
        return commentRepository.findByItemIdOrderByCreatedDesc(itemId)
                .stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new NotFoundException("Item with id=" + itemId + " not found"));
    }

    private void checkUserExists(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User with id=" + userId + " not found"));
    }

    private BookingShortDto toShortDto(Booking b) {
        if (b == null) return null;

        return BookingShortDto.builder()
                .id(b.getId())
                .bookerId(b.getBooker().getId())
                .start(b.getStart())
                .build();
    }

    @Override
    public ItemDto create(Long userId, ItemDto dto) {

        checkUserExists(userId);

        Item item = ItemMapper.toItem(dto, userId);
        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto dto) {

        Item item = getItemOrThrow(itemId);

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("User is not owner of item");
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            item.setName(dto.getName());
        }

        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            item.setDescription(dto.getDescription());
        }

        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }

        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto get(Long id) {

        Item item = getItemOrThrow(id);

        ItemDto dto = ItemMapper.toDto(item);
        dto.setComments(getComments(item.getId()));

        return dto;
    }

    @Override
    public List<ItemDto> getOwnerItems(Long userId) {

        checkUserExists(userId);

        List<Item> items = itemRepository.findByOwnerId(userId);

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();

        List<Booking> bookings =
                bookingRepository.findByItemIdInAndStatusOrderByStartAsc(
                        itemIds,
                        BookingStatus.APPROVED
                );

        List<Comment> comments =
                commentRepository.findByItemIdInOrderByCreatedDesc(itemIds);

        Map<Long, List<Booking>> bookingsByItem =
                bookings.stream()
                        .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        Map<Long, List<CommentDto>> commentsByItem =
                comments.stream()
                        .collect(Collectors.groupingBy(
                                c -> c.getItem().getId(),
                                Collectors.mapping(CommentMapper::toDto, Collectors.toList())
                        ));

        return items.stream()
                .map(item -> {

                    ItemDto dto = ItemMapper.toDto(item);

                    List<Booking> itemBookings =
                            bookingsByItem.getOrDefault(item.getId(), List.of());

                    Booking last = itemBookings.stream()
                            .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                            .max(Comparator.comparing(Booking::getStart))
                            .orElse(null);

                    Booking next = itemBookings.stream()
                            .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                            .min(Comparator.comparing(Booking::getStart))
                            .orElse(null);

                    dto.setLastBooking(toShortDto(last));
                    dto.setNextBooking(toShortDto(next));

                    dto.setComments(
                            commentsByItem.getOrDefault(item.getId(), List.of())
                    );

                    return dto;
                })
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {

        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.search(text).stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto dto) {

        checkUserExists(userId);

        Item item = getItemOrThrow(itemId);

        boolean hasBooking = bookingRepository
                .existsByItemIdAndBookerIdAndStatusAndEndBefore(
                        itemId,
                        userId,
                        BookingStatus.APPROVED,
                        LocalDateTime.now()
                );

        if (!hasBooking) {
            throw new ValidationException("User has not completed booking");
        }

        Comment comment = CommentMapper.toEntity(dto);
        comment.setItem(item);
        comment.setAuthor(userRepository.getReferenceById(userId));
        comment.setCreated(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);

        return CommentMapper.toDto(saved);
    }
}