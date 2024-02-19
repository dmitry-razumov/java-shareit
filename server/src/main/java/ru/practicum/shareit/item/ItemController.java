package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.ComponentScan;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@ComponentScan(basePackageClasses = ItemMapper.class)
public class ItemController {
    private final ItemService service;
    private final ItemMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody ItemDto itemDto) {
        log.info("POST /items with body {} and X-Sharer-User-Id={} ", itemDto, userId);
        return mapper.toItemDto(service.create(mapper.toItem(itemDto), userId, itemDto.getRequestId()));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long id,
                          @RequestBody ItemDto itemDto) {
        log.info("PATCH /items/{} with body {} and X-Sharer-User-Id={} ", id, itemDto, userId);
        itemDto.setId(id);
        return mapper.toItemDto(service.update(mapper.toItem(itemDto), userId));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "20") int size) {
        log.info("GET /items/?from={}&size={} X-Sharer-User-Id={}",
                from, size, ownerId);
        return mapper.toItemDto(service.getItemsByOwnerId(ownerId, from, size));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long id) {
        log.info("GET /items/{} X-Sharer-User-Id={} ", id, userId);
        return mapper.toItemDto(service.getById(id, userId));
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getByNameOrDescription(@RequestParam String text,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "20") int size) {
        log.info("GET /items/search?text={}&from={}&size={}", text, from, size);
        return mapper.toItemDto(service.getItemsByNameOrDescription(text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                 @PathVariable long itemId,
                                 @RequestBody CommentDto commentDto) {
        log.info("POST /items/{}/comment with body {} and X-Sharer-User-Id={}",
                itemId, commentDto, bookerId);
        return mapper.commentToDto(
                service.addComment(bookerId, itemId, mapper.dtoToComment(commentDto)));
    }
}
