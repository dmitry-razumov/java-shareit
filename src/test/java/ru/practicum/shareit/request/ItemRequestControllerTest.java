package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemRequestService itemRequestService;

    static User createOwner() {
        return User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.com")
                .build();
    }

    static User createRequester() {
        return User.builder()
                .id(2L)
                .name("requester")
                .email("requester@mail.com")
                .build();
    }

    static Item createItem(User owner) {
        return Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .owner(owner)
                .available(Boolean.TRUE)
                .comments(Collections.emptyList())
                .request(ItemRequest.builder().id(1L).build())
                .build();
    }

    static ItemRequest createRequest(User requester, Item item) {
        return ItemRequest.builder()
                .id(1)
                .created(LocalDateTime.now())
                .items(List.of(item))
                .description("requestDescription")
                .requester(requester)
                .build();
    }

    static ItemRequestDto createDto() {
        return ItemRequestDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("requestDescription")
                .build();
    }

    @Test
    @SneakyThrows
    void shouldCreateRequest() {
        User owner = createOwner();
        User requester = createRequester();
        Item item = createItem(owner);
        ItemRequest itemRequest = createRequest(requester, item);
        ItemRequestDto itemRequestDto = createDto();
        when(itemRequestService.create(any(), anyLong()))
                .thenReturn(itemRequest);
        String json = mapper.writeValueAsString(itemRequestDto);
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", requester.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(status().is2xxSuccessful(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.description").value(itemRequest.getDescription()));
    }

    @Test
    @SneakyThrows
    void shouldNotCreateRequestWithoutUserId() {
        User owner = createOwner();
        User requester = createRequester();
        Item item = createItem(owner);
        ItemRequest itemRequest = createRequest(requester, item);
        ItemRequestDto itemRequestDto = createDto();
        when(itemRequestService.create(any(), anyLong()))
                .thenReturn(itemRequest);
        String json = mapper.writeValueAsString(itemRequestDto);
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is5xxServerError());
        verify(itemRequestService, never()).create(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void shouldGetAllOwnerRequests() {
        User owner = createOwner();
        User requester = createRequester();
        Item item = createItem(owner);
        ItemRequest itemRequest = createRequest(requester, item);
        when(itemRequestService.getAllByRequesterId(anyLong()))
                .thenReturn(List.of(itemRequest));
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", requester.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().is2xxSuccessful(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[0].description").value(itemRequest.getDescription()));
    }

    @Test
    @SneakyThrows
    void shouldNotGetAllOwnerRequestsWithoutUserId() {
        User owner = createOwner();
        User requester = createRequester();
        Item item = createItem(owner);
        ItemRequest itemRequest = createRequest(requester, item);
        when(itemRequestService.getAllByRequesterId(anyLong()))
                .thenReturn(List.of(itemRequest));
        mockMvc.perform(get("/requests")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().is5xxServerError());
        verify(itemRequestService, never()).getAllByRequesterId(anyLong());
    }

    @Test
    @SneakyThrows
    void shouldGetAllRequests() {
        User owner = createOwner();
        User requester = createRequester();
        Item item = createItem(owner);
        ItemRequest itemRequest = createRequest(requester, item);
        when(itemRequestService.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequest));
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", requester.getId())
                        .param("from", "0")
                        .param("size", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().is2xxSuccessful(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[0].description").value(itemRequest.getDescription()));
    }

    @Test
    @SneakyThrows
    void shouldNotGetAllRequestsWithoutUserId() {
        User owner = createOwner();
        User requester = createRequester();
        Item item = createItem(owner);
        ItemRequest itemRequest = createRequest(requester, item);
        when(itemRequestService.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequest));
        mockMvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().is5xxServerError());
        verify(itemRequestService, never()).getAll(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void shouldGetRequestById() {
        User owner = createOwner();
        User requester = createRequester();
        Item item = createItem(owner);
        ItemRequest itemRequest = createRequest(requester, item);
        when(itemRequestService.getById(anyLong(), anyLong()))
                .thenReturn(itemRequest);
        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", requester.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().is2xxSuccessful(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.description").value(itemRequest.getDescription()));
    }

    @Test
    @SneakyThrows
    void shouldNotGetRequestByIdWithoutUserId() {
        User owner = createOwner();
        User requester = createRequester();
        Item item = createItem(owner);
        ItemRequest itemRequest = createRequest(requester, item);
        when(itemRequestService.getById(anyLong(), anyLong()))
                .thenReturn(itemRequest);
        mockMvc.perform(get("/requests/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().is5xxServerError());
        verify(itemRequestService, never()).getById(anyLong(), anyLong());
    }
}
