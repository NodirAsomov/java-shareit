package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequest toEntity(ItemRequestDto dto, User requestor) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .created(LocalDateTime.now())
                .requestor(requestor)
                .build();
    }

    public ItemRequestResponseDto toDto(ItemRequest request, List<RequestItemDto> items) {
        return ItemRequestResponseDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(items)
                .build();
    }

    public RequestItemDto toRequestItemDto(Item item) {
        return RequestItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }
}
