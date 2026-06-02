package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final ItemRequestService service;


    @PostMapping
    public ItemRequestDto create(@RequestHeader(USER_HEADER) Long userId,
                                 @RequestBody ItemRequestDto dto) {
        return service.create(userId, dto);
    }


    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@PathVariable Long requestId) {
        return service.getById(requestId);
    }


    @GetMapping
    public List<ItemRequestDto> getAll() {
        return service.getAll();
    }


    @GetMapping("/my")
    public List<ItemRequestDto> getByUser(@RequestHeader(USER_HEADER) Long userId) {
        return service.getByUser(userId);
    }
}