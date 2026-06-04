
package ru.practicum.shareit.user;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto dto) {
        return service.create(dto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id,
                          @Valid @RequestBody UserDto dto) {
        return service.update(id, dto);
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        return service.get(id);
    }
}