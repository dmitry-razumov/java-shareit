package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ItemServiceTest {
    @MockBean
    ItemRepository itemRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    ItemRequestRepository itemRequestRepository;
    @MockBean
    BookingRepository bookingRepository;
    @MockBean
    CommentRepository commentRepository;
    @Autowired
    ItemService itemService;
    private User owner;
    private Item item;
    private User booker;
    private Booking booking;
    private Comment comment;
    private Booking lastBooking;


    static User createOwner() {
        return User.builder()
                .id(1)
                .name("ownerName")
                .email("owner@mail.com")
                .build();
    }

    static Item createItem(User owner) {
        return Item.builder()
                .id(1)
                .name("itemName")
                .description("itemDescription")
                .owner(owner)
                .available(Boolean.TRUE)
                .comments(Collections.emptyList())
                .request(ItemRequest.builder().id(1).build())
                .build();
    }

    static User createBooker() {
        return User.builder()
                .id(2L)
                .name("bookerName")
                .email("booker@mail.com")
                .build();
    }

    static Booking createBooking(User booker, Item item) {
        return Booking.builder()
                .id(1L)
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(3))
                .build();
    }

    static Comment createComment(User booker, Item item) {
        return Comment.builder()
                .id(1L)
                .item(item)
                .created(LocalDateTime.now())
                .author(booker)
                .text("text")
                .build();
    }

    static Booking createLastBooking(User owner, LocalDateTime created, Item item) {
        return Booking.builder()
                .id(1L)
                .start(created.minusDays(2))
                .end(created.minusDays(1))
                .item(item)
                .booker(owner)
                .status(Status.WAITING)
                .build();
    }

    @BeforeEach
    void beforeEach() {
        owner = createOwner();
        item = createItem(owner);
        booker = createBooker();
        booking = createBooking(booker, item);
        comment = createComment(booker, item);
        lastBooking = createLastBooking(owner, LocalDateTime.now(), item);
    }

    @Test
    void shouldCreateItemWithoutRequestId() {
        item.setRequest(null);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> {
            Item newItem = invocationOnMock.getArgument(0);
            newItem.setId(1L);
            newItem.setComments(Collections.emptyList());
            return newItem;
        });
        Item resultItem = itemService.create(Item.builder()
                .id(0)
                .name("itemName")
                .description("itemDescription")
                .available(Boolean.TRUE)
                .build(),
                1L, null);
        assertThat(resultItem, equalTo(item));
    }

    @Test
    void shouldCreateItemWithRequestId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(ItemRequest.builder().id(1).build()));
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> {
            Item newItem = invocationOnMock.getArgument(0);
            newItem.setId(1L);
            newItem.setComments(Collections.emptyList());
            return newItem;
        });
        Item resultItem = itemService.create(Item.builder()
                        .id(0)
                        .name("itemName")
                        .description("itemDescription")
                        .available(Boolean.TRUE)
                        .build(),
                1L, 1L);
        assertThat(resultItem, equalTo(item));
    }

    @Test
    void shouldThrowOnCreateItemWhenRequestNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> {
            Item newItem = invocationOnMock.getArgument(0);
            newItem.setId(1L);
            newItem.setComments(Collections.emptyList());
            return newItem;
        });
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.create(Item.builder()
                                .id(0)
                                .name("itemName")
                                .description("itemDescription")
                                .available(Boolean.TRUE)
                                .build(),
                        1L, 10L));
        assertThat(exception.getMessage(), equalTo("itemRequest с id=10 не найдена"));
    }

    @Test
    void shouldThrowOnCreateItemWhenUserNotFound() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(itemRepository.save(any())).thenAnswer(invocationOnMock -> {
            Item item = invocationOnMock.getArgument(0);
            item.setId(1L);
            item.setComments(Collections.emptyList());
            return item;
        });
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.create(Item.builder()
                                .id(0)
                                .name("itemName")
                                .description("itemDescription")
                                .available(Boolean.TRUE)
                                .build(),
                        99L, 1L));
        assertThat(exception.getMessage(), equalTo("Собственник вещи не найден!"));
    }

    @Test
    void shouldUpdateItem() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Item result = itemService.update(item, owner.getId());
        assertThat(result, equalTo(item));
        assertThat(item.getOwner(), equalTo(owner));
    }

    @Test
    void shouldThrowOnUpdateItemWithoutOwnerEntity() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.update(item, owner.getId()));
        assertThat(exception.getMessage(), equalTo("Item с id=1 не найдена"));
    }

    @Test
    void shouldThrowOnUpdateItemWithWrongOwnerId() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.update(item, 99L));
        assertThat(exception.getMessage(), equalTo("Собственник вещи не совпадает"));
    }

    @Test
    void shouldDeleteItem() {
        itemRepository.save(item);
        itemRepository.deleteById(item.getId());
        assertThat(itemRepository.existsById(item.getId()), equalTo(false));
        verify(itemRepository, times(1)).deleteById(item.getId());
    }

    @Test
    void shouldGetAllItemsByOwner() {
        Item item1 = createItem(owner);
        Item item2 = createItem(owner);
        item2.setId(2L);
        List<Item> expectedItemList = List.of(item1, item2);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerIdOrderById(anyLong(), any()))
                .thenReturn(List.of(item1, item2));
        List<Item> resultItemList = itemService.getItemsByOwnerId(1L, 0, 20);
        assertThat(resultItemList, equalTo(expectedItemList));
    }

    @Test
    void shouldGetItemById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDescItemIdDesc(anyLong(), any(), any()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(anyLong(), any(), any()))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(item.getId()))
                .thenReturn(List.of(comment));
        Item result = itemService.getById(item.getId(), owner.getId());
        assertThat(item.getId(), equalTo(result.getId()));
    }

    @Test
    void shouldThrownOnGetItemWithoutItemEntity() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getById(item.getId(), owner.getId()));
        assertThat(exception.getMessage(), equalTo("Item с id=1 не найдена"));
    }

    @Test
    void shouldGetItemByNameOrDescription() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(
                anyString(), anyString(), anyBoolean(), any()))
                .thenReturn(List.of(item));
        List<Item> result = itemService.getItemsByNameOrDescription("itemName", 0, 20);
        assertThat(result.get(0).getId(), equalTo(item.getId()));
    }

    @Test
    void shouldGetNoneItemByNameOrDescriptionWithoutText() {
        itemRepository.save(item);
        List<Item> result = itemService.getItemsByNameOrDescription("", 0, 20);
        assertThat(result.size(), equalTo(0));
    }

    @Test
    void shouldCreateCommentForItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(ItemRequest.builder().id(1).build()));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenAnswer(invocationOnMock -> {
            Comment newComment = invocationOnMock.getArgument(0);
            newComment.setId(1L);
            newComment.setCreated(comment.getCreated());
            return newComment;
        });
        Comment resultComment = itemService.addComment(3L, 1L,
                Comment.builder()
                        .text("text")
                        .build());
        assertThat(resultComment, equalTo(comment));
    }

    @Test
    void shouldThrowOnCreateCommentForItemWhenNoItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(List.of(lastBooking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.addComment(3L, 99L,
                        Comment.builder()
                                .text("text")
                                .build()));
        assertThat(exception.getMessage(), equalTo("Item с id=99 не найдена"));
    }

    @Test
    void shouldThrownOnAddCommentWithoutUserBookings() {
        when(bookingRepository.findAllByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(Collections.emptyList());
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.addComment(booker.getId(), item.getId(), comment));
        assertThat(exception.getMessage(), equalTo("У данного пользователя нет бронирований"));
    }

    @Test
    void shouldThrownOnAddCommentWithoutUserEntity() {
        when(bookingRepository.findAllByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(List.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addComment(booker.getId(), item.getId(), comment));
        assertThat(exception.getMessage(), equalTo("Booker с id=2 не найден"));
    }
}
