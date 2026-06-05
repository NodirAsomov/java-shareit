package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    private User getUserOrThrow(Long id) {
        User user = storage.get(id);
        if (user == null) {
            throw new NotFoundException("User with id=" + id + " not found");
        }
        return user;
    }

    @Override
    public UserDto create(UserDto dto) {

        if (storage.emailExists(dto.getEmail())) {
            throw new ConflictException("Email exists");
        }

        User user = UserMapper.toUser(dto);
        return UserMapper.toDto(storage.create(user));
    }

    @Override
    public UserDto update(Long id, UserDto dto) {

        User user = getUserOrThrow(id);

        if (dto.getEmail() != null
                && storage.emailExistsForOtherUser(dto.getEmail(), id)) {
            throw new ConflictException("Email exists");
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            user.setEmail(dto.getEmail());
        }

        return UserMapper.toDto(storage.update(user));
    }

    @Override
    public UserDto get(Long id) {
        return UserMapper.toDto(getUserOrThrow(id));
    }

    @Override
    public void delete(Long id) {
        getUserOrThrow(id);
        storage.delete(id);
    }
}