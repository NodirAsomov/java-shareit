package ru.practicum.shareit;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.booking.dto.ItemDto;
import ru.practicum.shareit.client.ItemClient;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody @Valid ItemDto dto) {

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

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam String text) {

        return client.search(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody @Valid CommentDto dto) {

        return client.addComment(userId, itemId, dto);
    }
}
