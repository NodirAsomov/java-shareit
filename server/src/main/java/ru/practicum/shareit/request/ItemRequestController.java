


package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ItemRequestDto;
import ru.practicum.dto.RequestDto;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody RequestDto dto) {

        return service.create(userId, dto.getDescription());
    }

    @GetMapping
    public List<ItemRequestDto> getOwn(
            @RequestHeader("X-Sharer-User-Id") Long userId) {

        return service.getOwn(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(
            @RequestHeader("X-Sharer-User-Id") Long userId) {

        return service.getAllExceptUser(userId);
    }

    @GetMapping("/{id}")
    public ItemRequestDto getById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long id) {

        return service.getById(userId, id);
    }
}

