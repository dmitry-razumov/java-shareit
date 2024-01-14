package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage storage;

    @Override
    public User create(User user) {
        if (storage.isEmailExist(user)) {
            throw new ValidationException("User с таким Email уже существует");
        }
        return storage.create(user);
    }

    @Override
    public User update(User user) {
        User updatedUser = storage.getById(user.getId());
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (storage.isEmailExist(user)) {
                throw new ValidationException("User с таким Email уже существует");
            }
            updatedUser.setEmail(user.getEmail());
        }
        return storage.update(updatedUser);
    }

    @Override
    public void delete(long id) {
        storage.delete(id);
    }

    @Override
    public User getById(long id) {
        return storage.getById(id);
    }

    @Override
    public List<User> getAll() {
        return storage.getAll();
    }
}
