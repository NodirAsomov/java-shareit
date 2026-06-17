package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.RequestClient;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestClient client;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody Object dto) {

        return client.create(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwn(
            @RequestHeader("X-Sharer-User-Id") Long userId) {

        return client.getOwn(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam int from,
            @RequestParam int size) {

        return client.getAll(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long id) {

        return client.getById(userId, id);
    }
}