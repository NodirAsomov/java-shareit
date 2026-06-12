package ru.practicum.shareit.request.mapper;

import ru.practicum.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public class ItemRequestMapper {

    public static ItemRequest toEntity(ItemRequestDto dto, Long userId) {
        ItemRequest request = new ItemRequest();

        request.setDescription(dto.getDescription());
        request.setRequesterId(userId);

        return request;
    }

    public static ItemRequestDto toDto(ItemRequest request) {
        if (request == null) {
            return null;
        }

        List<ItemRequestDto.ItemResponse> itemDtos =
                request.getItems() == null
                        ? List.of()
                        : request.getItems().stream()
                        .map(ItemRequestMapper::toItemResponse)
                        .toList();

        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                itemDtos
        );
    }

    private static ItemRequestDto.ItemResponse toItemResponse(Item item) {
        return new ItemRequestDto.ItemResponse(
                item.getId(),
                item.getName(),
                item.getOwner() != null ? item.getOwner().getId() : null
        );
    }
}