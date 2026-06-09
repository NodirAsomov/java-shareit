package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

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
                .map(c -> CommentDto.builder()
                        .id(c.getId())
                        .text(c.getText())
                        .authorName(c.getAuthor().getName())
                        .created(c.getCreated())
                        .build())
                .toList();
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new NotFoundException("Item with id=" + itemId + " not found"));
    }

    @Override
    public ItemDto create(Long userId, ItemDto dto) {

        userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User with id=" + userId + " not found"));

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

        List<CommentDto> comments = getComments(item.getId())
                .stream()
                .map(c -> CommentDto.builder()
                        .id(c.getId())
                        .text(c.getText())
                        .created(c.getCreated())
                        .build())
                .toList();

        ItemDto dto = ItemMapper.toDto(item);
        dto.setComments(comments);

        return dto;
    }

    @Override
    public List<ItemDto> getOwnerItems(Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User not found"));

        return itemRepository.findByOwnerId(userId).stream()
                .map(item -> {

                    ItemDto dto = ItemMapper.toDto(item);

                    List<CommentDto> comments = getComments(item.getId())
                            .stream()
                            .map(c -> CommentDto.builder()
                                    .id(c.getId())
                                    .text(c.getText())
                                    .created(c.getCreated())
                                    .build())
                            .toList();

                    dto.setComments(comments);

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

        userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User not found"));

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

        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setItem(item);
        comment.setAuthor(userRepository.getReferenceById(userId));
        comment.setCreated(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);

        return CommentDto.builder()
                .id(saved.getId())
                .text(saved.getText())
                .authorName(saved.getAuthor().getName())
                .created(saved.getCreated())
                .build();
    }
}