package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.HashMap;

import java.util.Map;

@Repository
public class UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 1;

    public User create(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public User get(Long id) {
        return users.get(id);
    }

    public Collection<User> getAll() {
        return users.values();
    }

    public void delete(Long id) {
        users.remove(id);
    }
}