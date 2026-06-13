package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.BookingClient;
import ru.practicum.shareit.dto.booking.BookingDto;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingClient client;

    public BookingController(BookingClient client) {
        this.client = client;
    }

    @PostMapping
    public Object create(@RequestHeader("X-Sharer-User-Id") Long userId,
                         @Valid @RequestBody BookingDto dto) {
        return client.create(userId, dto).getBody();
    }

    @PatchMapping("/{id}")
    public Object approve(@PathVariable Long id,
                          @RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestParam Boolean approved) {
        return client.approve(id, userId, approved).getBody();
    }

    @GetMapping("/{id}")
    public Object get(@PathVariable Long id,
                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.get(id, userId).getBody();
    }
}