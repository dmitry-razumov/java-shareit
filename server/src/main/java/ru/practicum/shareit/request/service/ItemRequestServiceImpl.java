package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.PageRequestCustom;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemRequest create(ItemRequest itemRequest, long userId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден!"));
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest newItemRequest = itemRequestRepository.save(itemRequest);
        log.info("создан запрос - {} от пользователя с id={}", newItemRequest, userId);
        return newItemRequest;
    }

    @Override
    public List<ItemRequest> getAllByRequesterId(long requesterId) {
        userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + requesterId + " не найден"));
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(requesterId);
        log.info("получены запросы - {} для пользователя с id={}", itemRequestList, requesterId);
        return itemRequestList;
    }

    @Override
    public List<ItemRequest> getAll(long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        Pageable page = PageRequestCustom.get(from, size, "created");
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequesterIdNot(userId, page);
        log.info("получена page from={} size={} с запросами других пользователей - {} для пользователя с id={}",
                from / size, size, itemRequestList, userId);
        return itemRequestList;
    }

    @Override
    public ItemRequest getById(long userId, long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id=" + requestId + " не найден"));
        log.info("получен запрос - {} для пользователя с id={}",
                itemRequest, userId);
        return itemRequest;
    }
}
