package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    @Override
    public UserDto create(UserDto dto) {

        if (dto.getEmail() == null || dto.getEmail().isBlank()
                || !dto.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }

        if (storage.existsByEmail(dto.getEmail())) {
            throw new IllegalStateException("Email already exists");
        }

        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }

        User user = UserMapper.fromDto(dto);
        return UserMapper.toDto(storage.add(user));
    }

    @Override
    public UserDto update(Long id, UserDto dto) {

        User user = storage.getById(id);

        if (user == null) {
            throw new NoSuchElementException("User not found");
        }

        if (dto.getEmail() != null) {

            if (!dto.getEmail().contains("@")) {
                throw new IllegalArgumentException("Invalid email");
            }

            if (!dto.getEmail().equals(user.getEmail())
                    && storage.existsByEmail(dto.getEmail())) {
                throw new IllegalStateException("Email already exists");
            }

            user.setEmail(dto.getEmail());
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }

        return UserMapper.toDto(storage.update(user));
    }

    @Override
    public UserDto getById(Long id) {
        User user = storage.getById(id);

        if (user == null) {
            throw new NoSuchElementException("User not found");
        }

        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return storage.getAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        storage.delete(id);
    }
}