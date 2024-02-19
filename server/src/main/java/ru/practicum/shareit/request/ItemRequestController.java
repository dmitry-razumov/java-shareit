package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@ComponentScan(basePackageClasses = ItemRequestMapper.class)
public class ItemRequestController {
    private final ItemRequestService service;
    private final ItemRequestMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto create(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST /requests with body {} and X-Sharer-User-Id={}", itemRequestDto, userId);
        return mapper.toItemRequestDto(service.create(mapper.toItemRequest(itemRequestDto), userId));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getAllByRequesterId(@RequestHeader(name = "X-Sharer-User-Id") long requesterId) {
        log.info("GET /requests X-Sharer-User-Id={}", requesterId);
        return mapper.toItemRequestDto(service.getAllByRequesterId(requesterId));
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getAll(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                       @RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "20") int size) {
        log.info("GET /requests/all?from={}&size={} X-Sharer-User-Id={}", from, size, userId);
        return mapper.toItemRequestDto(service.getAll(userId, from, size));
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto getById(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                  @PathVariable long requestId) {
        log.info("GET /requests/{} X-Sharer-User-Id={}", requestId, userId);
        return mapper.toItemRequestDto(service.getById(userId, requestId));
    }
}
