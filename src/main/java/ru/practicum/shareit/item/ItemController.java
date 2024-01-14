package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;
    private final ItemMapper mapper;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        return mapper.toItemDto(service.create(mapper.toItem(itemDto), userId));
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long id,
                          @RequestBody ItemDto itemDto) {
        itemDto.setId(id);
        return mapper.toItemDto(service.update(mapper.toItem(itemDto), userId));
    }

    @GetMapping
    public List<ItemDto> getItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return mapper.toItemDto(service.getItemsByOwnerId(ownerId));
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable long id) {
        return mapper.toItemDto(service.getById(id));
    }

    @GetMapping("/search")
    public List<ItemDto> getByNameOrDescription(@RequestParam String text) {
        return mapper.toItemDto(service.getItemsByNameOrDescription(text));
    }
}
