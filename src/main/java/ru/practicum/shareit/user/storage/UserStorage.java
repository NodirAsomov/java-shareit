package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.HashMap;

import java.util.Map;

@Repository
public class UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    public User create(User u) {
        u.setId(id++);
        users.put(u.getId(), u);
        return u;
    }

    public User get(Long id) {
        return users.get(id);
    }

    public Collection<User> getAll() {
        return users.values();
    }

    public User update(User u) {
        users.put(u.getId(), u);
        return u;
    }
}