package ru.practicum.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.clients.RequestClient;
import ru.practicum.dto.RequestDto;


@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final RequestClient client;

    @PostMapping
    public Object create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody RequestDto dto) {

        return client.create(userId, dto);
    }

    @GetMapping
    public Object getOwn(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getOwn(userId);
    }

    @GetMapping("/all")
    public Object getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getAll(userId);
    }

    @GetMapping("/{id}")
    public Object getById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long id) {

        return client.getById(userId, id);
    }
}