package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;


public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestCreateDto dto);

    List<ItemRequestDto> getOwnRequests(Long userId);

    ItemRequestDto getById(Long userId, Long requestId);

    List<ItemRequestDto> getAll(Long userId, int from, int size);
}