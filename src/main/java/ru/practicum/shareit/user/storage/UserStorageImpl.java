package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public User create(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        log.info("создан пользователь - {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        log.info("обновлен пользователь - {}", user);
        return user;
    }

    @Override
    public void delete(long id) {
        users.remove(id);
        log.info("удален пользователь с id - {}", id);
    }

    @Override
    public User getById(long id) {
        User user = users.get(id);
        log.info("получен пользователь c id - {} {}", id, user);
        return user;
    }

    @Override
    public List<User> getAll() {
        List<User> userList = new ArrayList<>(users.values());
        log.info("получены все пользователи {}", userList);
        return userList;
    }

    public boolean isEmailExist(User user) {
        return users.values().stream()
                .filter(other -> other.getId() != user.getId())
                .anyMatch(other -> other.getEmail().equals(user.getEmail()));
    }
}
