package ru.practicum.shareit.user.mapper;

import ru.practicum.dto.UserDto;
import ru.practicum.shareit.user.User;

public class UserMapper {

    public static User toUser(UserDto dto) {
        User u = new User();
        u.setName(dto.getName());
        u.setEmail(dto.getEmail());
        return u;
    }

    public static UserDto toDto(User u) {
        UserDto dto = new UserDto();
        dto.setId(u.getId());
        dto.setName(u.getName());
        dto.setEmail(u.getEmail());
        return dto;
    }

    public static User toUserWithId(UserDto dto, Long id) {
        User u = new User();
        u.setId(id);
        u.setName(dto.getName());
        u.setEmail(dto.getEmail());
        return u;
    }
}