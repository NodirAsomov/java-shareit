package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

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
    public User create(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("Email exists");
        }

        return userRepository.save(user);
    }

    @Override
    public User update(Long id, User dto) {

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

        return userRepository.save(user);
    }

    @Override
    public User get(Long id) {
        return getUserOrThrow(id);
    }

    @Override
    public void delete(Long id) {
        getUserOrThrow(id);
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }
}