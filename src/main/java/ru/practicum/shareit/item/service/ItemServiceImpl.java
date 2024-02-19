package ru.practicum.shareit.item.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.utils.PageRequestCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    private User findUserByIdOrThrow(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Собственник вещи не найден!"));
    }

    private Item findItemByIdOrThrow(long ItemId) {
        return itemRepository.findById(ItemId)
                .orElseThrow(() -> new NotFoundException("Item с id=" + ItemId + " не найдена"));
    }

    @Override
    @Transactional
    public Item create(Item item, long userId, Long requestId) {
        User user = findUserByIdOrThrow(userId);
        item.setOwner(user);
        if (requestId != null) {
            item.setRequest(itemRequestRepository.findById(requestId)
                    .orElseThrow(() ->
                            new NotFoundException("itemRequest с id=" + requestId + " не найдена")));
        }
        Item newItem = itemRepository.save(item);
        log.info("создана item - {}, requests - {}", newItem, newItem.getRequest());
        return newItem;
    }

    @Override
    @Transactional
    public Item update(Item item, long userId) {
        Item updatedItem = findItemByIdOrThrow(item.getId());
        if (updatedItem.getOwner().getId() != userId) {
            throw new NotFoundException("Собственник вещи не совпадает");
        }
        if (item.getName() != null) {
            if (!item.getName().isBlank()) {
                updatedItem.setName(item.getName());
            } else {
                throw new ValidationException("name не должно быть пробелом или пустым");
            }
        }
        if (item.getDescription() != null) {
            if (!item.getDescription().isBlank()) {
                updatedItem.setDescription(item.getDescription());
            } else {
                throw new ValidationException("description не должно быть пробелом или пустым");
            }
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        log.info("обновлена item - {} request - {}", updatedItem, updatedItem.getRequest());
        return updatedItem;
    }

    @Override
    @Transactional
    public void delete(long id) {
        itemRepository.deleteById(id);
        log.info("удалена item - {}", id);
    }

    @Override
    public Item getById(long itemId, long userId) {
        User user = findUserByIdOrThrow(userId);
        Item item = findItemByIdOrThrow(itemId);
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        Item fullItem = constructFullItem(item, user, comments);
        log.info("получена item - {} {} request - {}", itemId, fullItem, fullItem.getRequest());
        return fullItem;
    }

    @Override
    public List<Item> getItemsByOwnerId(long ownerId, int from, int size) {
        User user = findUserByIdOrThrow(ownerId);
        Pageable page = PageRequestCustom.get(from, size);
        List<Item> itemList = itemRepository.findAllByOwnerIdOrderById(ownerId, page);
        List<Long> itemIds = itemList.stream()
                .map(item -> item.getId()).collect(Collectors.toList());
        List<Comment> comments = commentRepository.findAllByItemIds(itemIds);
        List<Item> fullItemList = new ArrayList<>();
        for (Item item : itemList) {
            fullItemList.add(constructFullItem(item, user, comments));
        }
        log.info("получена page from={} size={} cо всеми вещами для ownerId={} {}",
                from, size, ownerId, fullItemList);
        return fullItemList;
    }

    private Item constructFullItem(Item item, User user, List<Comment> comments) {
        Booking lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDescItemIdDesc(
                item.getId(), Status.APPROVED, LocalDateTime.now()).orElse(null);
        LocalDateTime from = (lastBooking == null) ? LocalDateTime.now() : lastBooking.getEnd();
        Booking nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                item.getId(), Status.APPROVED, from).orElse(null);
        log.info("lastbooking={}", lastBooking);
        log.info("nextbooking={}", nextBooking);

        return Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(user)
                .request(item.getRequest() != null ? item.getRequest() : null)
                .lastBooking(item.getOwner().getId() == user.getId() ? lastBooking : null)
                .nextBooking(item.getOwner().getId() == user.getId() ? nextBooking : null)
                .comments(comments)
                .build();
    }

    @Override
    public List<Item> getItemsByNameOrDescription(String text, int from, int size) {
        if (text.isEmpty()) {
            log.info("вещей для nameOrdescription={} нет", text);
            return Collections.emptyList();
        }
        Pageable page = PageRequestCustom.get(from, size);
        List<Item> itemList = itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(
                        text,text,true, page);
        log.info("получена page from={} size={} со всеми вещами для nameOrdescription={} {}",
                from, size, text, itemList);
        return itemList;
    }

    @Override
    public Comment addComment(long bookerId, long itemId, Comment comment) {
        if (bookingRepository.findAllByBookerIdAndItemIdAndEndBefore(bookerId, itemId, LocalDateTime.now()).isEmpty())
            throw new ValidationException("У данного пользователя нет бронирований");

        User user = userRepository.findById(bookerId).orElseThrow(() ->
                new NotFoundException("Booker с id=" + bookerId + " не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Item с id=" + itemId + " не найдена"));
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        log.info("сохранен комментарий {}", savedComment);
        return savedComment;
    }
}
