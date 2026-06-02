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

    private final UserStorage storage;


    @Override
    public UserDto create(UserDto dto) {

        if (dto.getEmail() == null) dto.setEmail("");
        if (dto.getName() == null) dto.setName("");

        User user = UserMapper.fromDto(dto);
        return UserMapper.toDto(storage.add(user));
    }


    @Override
    public UserDto update(Long id, UserDto dto) {

        User user = storage.getById(id);

        if (user == null) {
            user = new User();
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }

        if (dto.getName() != null) {
            user.setName(dto.getName());
        }

        return UserMapper.toDto(storage.update(user));
    }

    @Override
    public UserDto getById(Long id) {
        return UserMapper.toDto(storage.getById(id));
    }

    @Override
    public List<UserDto> getAll() {
        return storage.getAll().stream().map(UserMapper::toDto).toList();
    }

    @Override
    public void delete(Long id) {
        storage.delete(id);
    }
}