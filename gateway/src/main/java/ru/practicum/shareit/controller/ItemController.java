package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.dto.item.ItemDto;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemClient client;

    public ItemController(ItemClient client) {
        this.client = client;
    }

    @PostMapping
    public Object create(@RequestHeader("X-Sharer-User-Id") Long userId,
                         @Valid @RequestBody ItemDto dto) {
        return client.create(userId, dto).getBody();
    }

    @GetMapping("/{id}")
    public Object get(@PathVariable Long id,
                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.get(id, userId).getBody();
    }

    @GetMapping
    public Object getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getAll(userId).getBody();
    }
}