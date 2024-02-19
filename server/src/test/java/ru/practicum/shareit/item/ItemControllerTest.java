package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
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

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ItemService itemService;
    @MockBean
    CommentRepository commentRepository;
    private User owner;
    private Item item;
    private ItemDto itemDto;
    private List<Item> items;
    private User author;
    private Comment comment;
    private CommentDto commentDto;

    static ItemDto createItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(Boolean.TRUE)
                .requestId(1L)
                .build();
    }

    static User createOwner() {
        return User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.com")
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

    static Comment createComment(User author, Item item) {
        return Comment.builder()
                .id(1L)
                .text("commentText")
                .author(author)
                .created(LocalDateTime.now())
                .item(item)
                .build();
    }

    static CommentDto createCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .text("commentText")
                .build();
    }

    @BeforeEach
    void beforeEach() {
        owner = createOwner();
        item = createItem(owner);
        itemDto = createItemDto();
        items = List.of(createItem(owner));
        author = createOwner();
        comment = createComment(author, createItem(author));
        commentDto = createCommentDto();
    }

    @Test
    void shouldCreateItem() throws Exception {
        when(itemService.create(any(), anyLong(),anyLong()))
                .thenReturn(item);
        String json = mapper.writeValueAsString(itemDto);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().is2xxSuccessful(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("item"),
                        jsonPath("$.description").value("description"),
                        jsonPath("$.available").value("true")
                );
    }

    @Test
    void shouldNotCreateItemWithoutUserId() throws Exception {
        when(itemService.create(any(), anyLong(), anyLong()))
                .thenReturn(item);
        String json = mapper.writeValueAsString(itemDto);
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is5xxServerError());
        verify(itemService, never()).create(any(), anyLong(), anyLong());
    }

    @Test
    void shouldGetItemById() throws Exception {
        when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(item);
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", owner.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().is2xxSuccessful(),
                        jsonPath("$.id").value("1"),
                        jsonPath("$.name").value("item"),
                        jsonPath("$.description").value("description"),
                        jsonPath("$.available").value("true")
                );
    }

    @Test
    void shouldNotGetItemByIdWithNullPath() throws Exception {
        when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(item);
        mockMvc.perform(get("/items/null")
                        .header("X-Sharer-User-Id", owner.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(
                        status().is5xxServerError());
        verify(commentRepository, never()).findAllByItemId(anyLong());
    }

    @Test
    void shouldUpdateItem() throws Exception {
        when(itemService.update(any(), anyLong()))
                .thenReturn(item);
        String json = mapper.writeValueAsString(itemDto);
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().is2xxSuccessful(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("item"),
                        jsonPath("$.description").value("description"),
                        jsonPath("$.available").value("true")
                );
    }

    @Test
    void shouldNotUpdateItemWithoutUserId() throws Exception {
        when(itemService.update(any(), anyLong()))
                .thenReturn(item);
        String json = mapper.writeValueAsString(itemDto);
        mockMvc.perform(patch("/items/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is5xxServerError());
        verify(itemService, never()).update(any(), anyLong());
    }

    @Test
    void shouldGetOwnersItems() throws Exception {
        when(itemService.getItemsByOwnerId(anyLong(), anyInt(), anyInt()))
                .thenReturn(items);
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().is2xxSuccessful(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].id", Matchers.is(items.get(0).getId()), Long.class));
    }

    @Test
    void shouldNotGetOwnersItemsWithoutUserId() throws Exception {
        when(itemService.getItemsByOwnerId(anyLong(), anyInt(), anyInt()))
                .thenReturn(items);
        mockMvc.perform(get("/items")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
        verify(itemService, never()).getItemsByOwnerId(anyLong(), anyInt(), anyInt());
    }

    @Test
    void shouldFindItemByNameOrDescription() throws Exception {
        when(itemService.getItemsByNameOrDescription(anyString(), anyInt(), anyInt()))
                .thenReturn(items);
        mockMvc.perform(get("/items/search")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("text", "description"))
                .andExpectAll(status().is2xxSuccessful(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].id", Matchers.is(items.get(0).getId()), Long.class));
    }

    @Test
    void shouldNotFindItemByNameOrDescriptionWithoutText() throws Exception {
        when(itemService.getItemsByNameOrDescription(anyString(), anyInt(), anyInt()))
                .thenReturn(items);
        mockMvc.perform(get("/items/search")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
        verify(itemService, never()).getItemsByNameOrDescription(anyString(), anyInt(), anyInt());
    }

    @Test
    void shouldAddComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(comment);
        String json = mapper.writeValueAsString(commentDto);
        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", author.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(status().is2xxSuccessful(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.text").value("commentText")
                );
    }

    @Test
    void shouldNotAddCommentWithoutUserId() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(comment);
        String json = mapper.writeValueAsString(commentDto);
        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(status().is5xxServerError());
        verify(itemService, never()).addComment(anyLong(), anyLong(), any());
    }
}
