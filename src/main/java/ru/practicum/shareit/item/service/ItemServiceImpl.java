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

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto create(Long ownerId, ItemDto dto) {
        User owner = userStorage.getById(ownerId);


        Item item = ItemMapper.fromDto(dto);
        item.setOwner(owner);

        return ItemMapper.toDto(itemStorage.add(item));
    }


    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto dto) {

        Item item = itemStorage.getById(itemId);

        if (item == null) {
            return null;
        }

        if (item.getOwner() != null &&
                item.getOwner().getId().equals(ownerId)) {

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


        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto getById(Long itemId) {
        return ItemMapper.toDto(itemStorage.getById(itemId));
    }

    @Override
    public List<ItemDto> getOwnerItems(Long ownerId) {
        return itemStorage.getByOwnerId(ownerId)
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

        return itemStorage.getAll()
                .stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> {
                    String name = item.getName();
                    String desc = item.getDescription();

                    return (name != null && name.toLowerCase().contains(query))
                            || (desc != null && desc.toLowerCase().contains(query));
                })
                .map(ItemMapper::toDto)
                .toList();
    }
}