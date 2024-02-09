package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(User user) {
        User updatedUser = getById(user.getId());
        if (user.getName() != null) {
            if (!user.getName().isBlank()) {
                updatedUser.setName(user.getName());
            } else {
                throw new ValidationException("name не должно быть пробелом или пустым");
            }
        }
        if (user.getEmail() != null) {
            if (!user.getEmail().isBlank()) {
                updatedUser.setEmail(user.getEmail());
            } else {
                throw new ValidationException("email не должно быть пробелом или пустым");
            }
        }
        return updatedUser;
    }

    @Override
    @Transactional
    public void delete(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }
}
