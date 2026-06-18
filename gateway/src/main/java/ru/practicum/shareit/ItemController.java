package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.ItemDto;
import ru.practicum.shareit.client.ItemClient;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemDto dto) {

        return client.create(userId, dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long id) {

        return client.get(userId, id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long id,
            @RequestBody ItemDto dto) {

        return client.update(userId, id, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(
            @RequestHeader("X-Sharer-User-Id") Long userId) {

        return client.getAll(userId);
    }
}