package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    @Override
    public UserDto create(UserDto dto) {

        boolean exists = storage.getAll().stream()
                .anyMatch(u -> u.getEmail().equals(dto.getEmail()));

        if (exists) {
            throw new ConflictException("Email exists");
        }

        User u = UserMapper.toUser(dto);
        return UserMapper.toDto(storage.create(u));
    }

    @Override
    public UserDto update(Long id, UserDto dto) {

        User u = storage.get(id);
        if (u == null) throw new NotFoundException("User not found");

        if (dto.getEmail() != null &&
                storage.getAll().stream()
                        .anyMatch(x -> x.getEmail().equals(dto.getEmail()) && !x.getId().equals(id))) {
            throw new ConflictException("Email exists");
        }

        if (dto.getName() != null) u.setName(dto.getName());
        if (dto.getEmail() != null) u.setEmail(dto.getEmail());

        return UserMapper.toDto(storage.update(u));
    }

    @Override
    public UserDto get(Long id) {
        User u = storage.get(id);
        if (u == null) throw new NotFoundException("User not found");
        return UserMapper.toDto(u);
    }
}