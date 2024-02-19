package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItemRequest(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                         @Validated @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST /requests with body {} and X-Sharer-User-Id={}",
                itemRequestDto, userId);
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllItemRequestByRequesterId(
            @RequestHeader(name = "X-Sharer-User-Id") long requesterId) {
        log.info("GET /requests X-Sharer-User-Id={}", requesterId);
        return itemRequestClient.getAllItemRequestByRequesterId(requesterId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllItemRequest(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                    @RequestParam(defaultValue = "20") @Positive int size) {
        log.info("GET /requests/all?from={}&size={} X-Sharer-User-Id={}",
                from, size, userId);
        return itemRequestClient.getAllItemRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                     @PathVariable long requestId) {
        log.info("GET /requests/{} X-Sharer-User-Id={}", requestId, userId);
        return itemRequestClient.getItemById(userId, requestId);
    }
}
