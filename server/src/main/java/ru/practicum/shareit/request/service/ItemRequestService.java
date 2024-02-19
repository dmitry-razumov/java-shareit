package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;
import java.util.List;

public interface ItemRequestService {
    ItemRequest create(ItemRequest toItemRequest, long userId);

    List<ItemRequest> getAllByRequesterId(long requesterId);

    List<ItemRequest> getAll(long userId, int from, int size);

    ItemRequest getById(long userId, long requestId);
}
