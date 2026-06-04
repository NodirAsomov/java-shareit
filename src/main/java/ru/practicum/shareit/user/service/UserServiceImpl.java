package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto create(UserDto dto) {
        User user = UserMapper.toUser(dto);
        return UserMapper.toDto(userStorage.create(user));
    }

    @Override
    public UserDto update(Long id, UserDto dto) {
        User user = UserMapper.toUser(dto);
        user.setId(id);

        return UserMapper.toDto(userStorage.update(user));
    }

    @Override
    public UserDto get(Long id) {
        return UserMapper.toDto(userStorage.get(id));
    }

    @Override
    public List<UserDto> getAll() {
        return userStorage.getAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        userStorage.delete(id);
    }
}