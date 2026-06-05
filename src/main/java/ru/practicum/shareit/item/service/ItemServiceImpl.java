package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage storage;
    private final UserStorage userStorage;

    private Item getItemOrThrow(Long itemId) {
        Item item = storage.get(itemId);
        if (item == null) {
            throw new NotFoundException("Item with id=" + itemId + " not found");
        }
        return item;
    }

    @Override
    public ItemDto create(Long userId, ItemDto dto) {

        if (userStorage.get(userId) == null) {
            throw new NotFoundException("User with id=" + userId + " not found");
        }

        Item item = ItemMapper.toItem(dto, userId);
        return ItemMapper.toDto(storage.create(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto dto) {

        Item item = getItemOrThrow(itemId);

        if (!item.getOwnerId().equals(userId)) {
            throw new NotFoundException(
                    "User with id=" + userId +
                            " is not owner of item with id=" + itemId
            );
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

        return ItemMapper.toDto(storage.update(item));
    }

    @Override
    public ItemDto get(Long id) {
        Item item = getItemOrThrow(id);
        return ItemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getOwnerItems(Long userId) {

        if (userStorage.get(userId) == null) {
            throw new NotFoundException("User with id=" + userId + " not found");
        }

        return storage.getByOwnerId(userId).stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {

        if (text == null || text.isBlank()) {
            return List.of();
        }

        return storage.search(text).stream()
                .map(ItemMapper::toDto)
                .toList();
    }
}