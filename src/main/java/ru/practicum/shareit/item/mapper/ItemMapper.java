package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;

public class ItemMapper {

    public static ItemDto toDto(Item i) {
        ItemDto dto = new ItemDto();
        dto.setId(i.getId());
        dto.setName(i.getName());
        dto.setDescription(i.getDescription());
        dto.setAvailable(i.getAvailable());
        return dto;
    }
}