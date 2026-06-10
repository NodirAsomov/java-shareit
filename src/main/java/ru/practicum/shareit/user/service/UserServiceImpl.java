package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("User with id=" + id + " not found"));
    }

    @Override
    public UserDto create(UserDto dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email exists");
        }

        User user = UserMapper.toUser(dto);
        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto update(Long id, UserDto dto) {

        User user = getUserOrThrow(id);

        if (dto.getEmail() != null &&
                userRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new ConflictException("Email exists");
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            user.setEmail(dto.getEmail());
        }

        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto get(Long id) {
        return UserMapper.toDto(getUserOrThrow(id));
    }

    @Override
    public void delete(Long id) {
        getUserOrThrow(id);
        userRepository.deleteById(id);
    }
}