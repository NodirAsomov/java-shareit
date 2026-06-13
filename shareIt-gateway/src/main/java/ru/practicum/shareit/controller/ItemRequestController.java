package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemRequestClient;
import ru.practicum.shareit.dto.request.RequestCreateDto;

@RestController
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestClient client;

    public ItemRequestController(ItemRequestClient client) {
        this.client = client;
    }


    @PostMapping
    public ResponseEntity<Object> createRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody RequestCreateDto dto) {

        return client.createRequest(userId, dto);
    }


    @GetMapping
    public ResponseEntity<Object> getOwnRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId) {

        return client.getOwnRequests(userId);
    }


    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        return client.getAllRequests(userId, from, size);
    }


    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId) {

        return client.getRequestById(userId, requestId);
    }
}