package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestDto dto);

    ItemRequestDto getById(Long requestId);

    List<ItemRequestDto> getAll();

    List<ItemRequestDto> getByUser(Long userId);
}