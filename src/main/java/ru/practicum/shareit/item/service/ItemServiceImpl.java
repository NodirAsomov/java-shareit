package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto create(Long userId, ItemDto dto) {

        if (userId == null) {
            throw new IllegalArgumentException("User id is required");
        }

        User owner = userStorage.getById(userId);

        if (owner == null) {
            throw new NoSuchElementException("User not found");
        }

        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }

        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }

        if (dto.getAvailable() == null) {
            throw new IllegalArgumentException("Available is required");
        }

        Item item = ItemMapper.fromDto(dto);
        item.setOwner(owner);

        return ItemMapper.toDto(itemStorage.add(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto dto) {

        if (userId == null) {
            throw new IllegalArgumentException("User id is required");
        }

        Item item = itemStorage.getById(itemId);

        if (item == null) {
            throw new NoSuchElementException("Item not found");
        }

        if (!item.getOwner().getId().equals(userId)) {
            throw new NoSuchElementException("Only owner can update item");
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

        return ItemMapper.toDto(itemStorage.update(item));
    }

    @Override
    public ItemDto getById(Long id) {
        Item item = itemStorage.getById(id);

        if (item == null) {
            throw new NoSuchElementException("Item not found");
        }

        return ItemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getOwnerItems(Long userId) {
        return itemStorage.getByOwnerId(userId)
                .stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {

        if (text == null || text.isBlank()) {
            return List.of();
        }

        String query = text.toLowerCase();

        return itemStorage.getAll().stream()
                .filter(Item::getAvailable)
                .filter(item ->
                        (item.getName() != null && item.getName().toLowerCase().contains(query)) ||
                                (item.getDescription() != null && item.getDescription().toLowerCase().contains(query))
                )
                .map(ItemMapper::toDto)
                .toList();
    }
}