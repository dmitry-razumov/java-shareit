package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    TestEntityManager em;
    @Autowired
    ItemRequestRepository itemRequestRepository;

    private User owner;
    private User requester;
    private ItemRequest itemRequest;

    static User createOwner() {
        return User.builder()
                .name("owner")
                .email("owner@mail.com")
                .build();
    }

    static User createRequester() {
        return User.builder()
                .name("requester")
                .email("requester@mail.com")
                .build();
    }

    static Item createItem(User owner) {
        return Item.builder()
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
    }

    static ItemRequest createRequest(User requester, Item item) {
        return ItemRequest.builder()
                .created(LocalDateTime.now())
                .requester(requester)
                .description("requestDescription")
                .items(List.of(item))
                .build();
    }

    @BeforeEach
    void beforeEach() {
        owner = createOwner();
        requester = createRequester();
        Item item = createItem(owner);
        itemRequest = createRequest(requester, item);
        em.persist(owner);
        em.persist(requester);
        em.persist(item);
        em.persist(itemRequest);
    }

    @Test
    void shouldFindAllByRequesterId() {
        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequesterIdOrderByCreatedDesc(requester.getId());
        assertThat(itemRequests.size(), equalTo(1));
        assertThat(itemRequests.get(0), equalTo(itemRequest));
    }

    @Test
    void shouldFindNoneAllByNoExistRequesterId() {
        List<ItemRequest> emptyList = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(99L);
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void shouldFindRequestAnotherUsers() {
        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequesterIdNot(owner.getId(), PageRequest.of(0, 20));
        assertThat(itemRequests.size(), equalTo(1));
        assertThat(itemRequests.get(0), equalTo(itemRequest));
    }

    @Test
    void shouldFindNoneRequestAnotherUsersByNoExistRequester() {
        List<ItemRequest> emptyList = itemRequestRepository
                .findAllByRequesterIdNot(requester.getId(), PageRequest.of(0, 20));
        assertThat(emptyList.size(), equalTo(0));
    }
}
