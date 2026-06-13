package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.UserClient;
import ru.practicum.shareit.dto.user.UserDto;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserClient client;

    public UserController(UserClient client) {
        this.client = client;
    }

    @PostMapping
    public Object create(@Valid @RequestBody UserDto dto) {
        return client.create(dto).getBody();
    }

    @GetMapping("/{id}")
    public Object get(@PathVariable Long id,
                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.get(id, userId).getBody();
    }

    @PatchMapping("/{id}")
    public Object update(@PathVariable Long id,
                         @RequestHeader("X-Sharer-User-Id") Long userId,
                         @RequestBody UserDto dto) {
        return client.update(id, dto, userId).getBody();
    }

    @DeleteMapping("/{id}")
    public Object delete(@PathVariable Long id,
                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.delete(id, userId).getBody();
    }
}