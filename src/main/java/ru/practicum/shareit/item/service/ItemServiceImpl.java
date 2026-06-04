package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;


import java.util.List;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    @Override
    public ItemDto create(ItemDto dto, Long ownerId) {

        Item item = new Item(
                null,
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable(),
                ownerId
        );

        return ItemMapper.toDto(itemStorage.create(item));
    }

    @Override
    public ItemDto update(Long itemId,
                          Long ownerId,
                          ItemDto dto) {

        Item item = itemStorage.get(itemId);

        if (!item.getOwnerId().equals(ownerId)) {
            throw new RuntimeException("Редактировать может только владелец");
        }

        if (dto.getName() != null) {
            item.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }

        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }

        return ItemMapper.toDto(itemStorage.update(item));
    }

    @Override
    public ItemDto get(Long itemId) {
        return ItemMapper.toDto(itemStorage.get(itemId));
    }

    @Override
    public List<ItemDto> getOwnerItems(Long ownerId) {
        return itemStorage.getAll().stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {

        if (text.isBlank()) {
            return List.of();
        }

        String query = text.toLowerCase();

        return itemStorage.getAll().stream()
                .filter(Item::getAvailable)
                .filter(item ->
                        item.getName().toLowerCase().contains(query)
                                || item.getDescription().toLowerCase().contains(query))
                .map(ItemMapper::toDto)
                .toList();
    }
}