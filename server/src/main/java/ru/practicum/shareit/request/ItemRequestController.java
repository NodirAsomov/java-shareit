package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    public ItemRequestResponseDto create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemRequestDto dto) {
        return service.create(userId, dto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getOwn(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getOwn(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAll(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return service.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId) {
        return service.getById(userId, requestId);
    }
}
