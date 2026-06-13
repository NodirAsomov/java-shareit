package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemRequestCreateDto dto) {

        return service.create(userId, dto);
    }

    @GetMapping
    public List<ItemRequestDto> getOwn(
            @RequestHeader("X-Sharer-User-Id") Long userId) {

        return service.getOwnRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId) {

        return service.getById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam int from,
            @RequestParam int size) {

        return service.getAll(userId, from, size);
    }
}
