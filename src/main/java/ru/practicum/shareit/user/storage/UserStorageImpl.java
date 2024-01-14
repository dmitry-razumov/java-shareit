package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserStorageImpl implements UserStorage{
    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public User create(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

    @Override
    public User getById(long id) {
        return users.get(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    public boolean isEmailExist(User user) {
        return getAll().stream()
                .filter(other -> other.getId() != user.getId())
                .anyMatch(other -> other.getEmail().equals(user.getEmail()));
    }
}
