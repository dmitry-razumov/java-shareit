package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.interfaces.CreateItem;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;
    private final ItemMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Validated(CreateItem.class) @RequestBody ItemDto itemDto) {
        log.info("POST /items with body {} and X-Sharer-User-Id={} ", itemDto, userId);
        return mapper.toItemDto(service.create(mapper.toItem(itemDto), userId));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long id,
                          @RequestBody ItemDto itemDto) {
        log.info("PATCH /items/{{}} with body {} and X-Sharer-User-Id={} ", id, itemDto, userId);
        itemDto.setId(id);
        return mapper.toItemDto(service.update(mapper.toItem(itemDto), userId));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("GET /items with X-Sharer-User-Id={} ", ownerId);
        return mapper.toItemDto(service.getItemsByOwnerId(ownerId));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long id) {
        log.info("GET /items/{{}} X-Sharer-User-Id={} ", id, userId);
        return mapper.toItemDto(service.getById(id, userId));
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getByNameOrDescription(@RequestParam String text) {
        log.info("GET /items/search?text={}", text);
        return mapper.toItemDto(service.getItemsByNameOrDescription(text));
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                 @PathVariable long itemId,
                                 @Validated @RequestBody CommentDto commentDto) {
        log.info("POST /items/{{}}/comment with body {} and X-Sharer-User-Id={}",
                itemId, commentDto, bookerId);
        return mapper.commentToDto(
                service.addComment(bookerId, itemId, mapper.dtoToComment(commentDto)));
    }
}
