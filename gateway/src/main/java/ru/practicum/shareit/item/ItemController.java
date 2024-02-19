package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.interfaces.CreateItem;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @Validated(CreateItem.class) @RequestBody ItemDto itemDto) {
        log.info("POST /items with body {} and X-Sharer-User-Id={} ", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long id,
                                             @RequestBody ItemDto itemDto) {
        log.info("PATCH /items/{} with body {} and X-Sharer-User-Id={} ", id, itemDto, userId);
        return itemClient.updateItem(userId, id, itemDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                    @RequestParam(defaultValue = "20") @Positive int size) {
        log.info("GET /items/?from={}&size={} X-Sharer-User-Id={}", from, size, ownerId);
        return itemClient.getItemsByOwnerId(ownerId, from, size);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long id) {
        log.info("GET /items/{} X-Sharer-User-Id={} ", id, userId);
        return itemClient.getItemById(userId, id);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getByNameOrDescription(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam String text,
                                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                         @RequestParam(defaultValue = "20") @Positive int size) {
        log.info("GET /items/search?text={}&from={}&size={} X-Sharer-User-Id={}", text, from, size, userId);
        return itemClient.getItemByNameOrDescription(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                             @PathVariable long itemId,
                                             @Validated @RequestBody CommentDto commentDto) {
        log.info("POST /items/{}/comment with body {} and X-Sharer-User-Id={}", itemId, commentDto, bookerId);
        return itemClient.addComment(bookerId, itemId, commentDto);
    }

}
