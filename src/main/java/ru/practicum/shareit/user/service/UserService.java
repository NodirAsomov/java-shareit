package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;


public interface UserService {
    UserDto create(UserDto dto);

    UserDto update(Long id, UserDto dto);

    UserDto get(Long id);
}