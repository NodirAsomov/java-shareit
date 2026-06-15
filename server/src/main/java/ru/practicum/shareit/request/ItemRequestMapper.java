package ru.practicum.shareit.request;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;

import java.util.List;

public class ItemRequestMapper {

    public static ItemResponseDto toItemDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }

    public static ItemRequestDto toDto(ItemRequest request,
                                       List<Item> items) {

        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                items.stream()
                        .map(ItemRequestMapper::toItemDto)
                        .toList()
        );
    }
}