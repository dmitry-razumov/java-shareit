package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.exception.NotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ItemRequestServiceTest {
    @MockBean
    ItemRequestRepository itemRequestRepository;
    @MockBean
    UserRepository userRepository;
    @Autowired
    ItemRequestService itemRequestService;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private ItemRequest itemRequest3;
    private User requester1;

    @BeforeEach
    void beforeEach() {
        requester1 = User.builder()
                .id(1L)
                .name("userName1")
                .email("user1@mail.com")
                .build();
        User requester2 = User.builder()
                .id(2L)
                .name("userName2")
                .email("user2@mail.com")
                .build();

        LocalDateTime created1 = LocalDateTime.now();

        itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("request1")
                .created(created1)
                .requester(requester1)
                .items(null)
                .build();
        itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("request2")
                .created(created1)
                .requester(requester1)
                .items(null)
                .build();
        itemRequest3 = ItemRequest.builder()
                .id(3L)
                .description("request3")
                .created(created1)
                .requester(requester2)
                .items(null)
                .build();
    }

    @Test
    void shouldCreateItemRequest() {
        when(itemRequestRepository.save(any()))
                .thenAnswer(invocationOnMock -> {
                        ItemRequest itemRequest = invocationOnMock.getArgument(0);
                        itemRequest1.setCreated(itemRequest.getCreated());
                        itemRequest.setId(1L);
                        return itemRequest;
                });
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requester1));
        ItemRequest resultItemRequest = itemRequestService.create(
                new ItemRequest(0L, itemRequest1.getDescription(),
                        null, null, null), 1L);
        assertThat(itemRequest1, equalTo(resultItemRequest));
    }

    @Test
    void shouldThrowOnCreateItemRequestIfUserNotExists() {
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.create(new ItemRequest(0L, itemRequest1.getDescription(),
                        null, null, null), 99L)
        );
        assertThat(exception.getMessage(), equalTo("Пользователь не найден!"));
    }

    @Test
    void shouldGetRequestsByOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requester1));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(itemRequest1, itemRequest2));
        List<ItemRequest> resultItemRequests = itemRequestService.getAllByRequesterId(1L);
        assertThat(resultItemRequests.size(), equalTo(2));
        assertThat(resultItemRequests, equalTo(List.of(itemRequest1, itemRequest2)));
    }

    @Test
    void shouldGetAllRequests() {
        List<ItemRequest> requests = List.of(itemRequest1, itemRequest2, itemRequest3);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requester1));
        when(itemRequestRepository.findAllByRequesterIdNot(anyLong(), any()))
                .thenAnswer(invocationOnMock -> {
                    long ownerId = invocationOnMock.getArgument(0);
                    return requests.stream().filter(r -> r.getRequester().getId() != ownerId)
                            .collect(Collectors.toList());
                });
        List<ItemRequest> resultItemRequests = itemRequestService.getAll(1L, 0, 20);
        assertThat(resultItemRequests.size(), equalTo(1));
        assertThat(resultItemRequests, equalTo(List.of(itemRequest3)));
    }

    @Test
    void shouldGetRequestById() {
        List<ItemRequest> requests = List.of(itemRequest1, itemRequest2, itemRequest3);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requester1));
        when(itemRequestRepository.findById(anyLong()))
                .thenAnswer(invocationOnMock -> {
                    long requestId = invocationOnMock.getArgument(0);
                    return requests.stream().filter(r -> r.getId() == requestId).findFirst();
                });
        ItemRequest targetItemRequest = itemRequestService.getById(1L, 2L);
        assertThat(targetItemRequest, equalTo(itemRequest2));
    }
}
