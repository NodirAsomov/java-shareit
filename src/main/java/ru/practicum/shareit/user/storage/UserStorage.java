package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {

    User add(User user);

    User update(User user);

    User getById(Long id);

    List<User> getAll();

    void delete(Long id);

    boolean existsByEmail(String email);
}