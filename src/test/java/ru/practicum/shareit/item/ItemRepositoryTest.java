package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    TestEntityManager em;
    @Autowired
    ItemRepository itemRepository;
    private User owner;
    private Item item;

    static User createOwner() {
        return User.builder()
                .name("owner")
                .email("owner@mail.com")
                .build();
    }

    static Item createItem(User owner) {
        return Item.builder()
                .name("item")
                .description("description")
                .available(Boolean.TRUE)
                .owner(owner)
                .build();
    }

    @BeforeEach
    void beforeEach() {
        owner = createOwner();
        item = createItem(owner);
        em.persist(owner);
        em.persist(item);
    }

    @Test
    void shouldFindAllByOwnerId() {
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(owner.getId(), PageRequest.of(0, 20));
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0), equalTo(item));
    }

    @Test
    void shouldFindNoneAllByNoExistOwnerId() {
        List<Item> emptyList = itemRepository.findAllByOwnerIdOrderById(99L, PageRequest.of(0, 20));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void shouldFindByNameOrDescription() {
        List<Item> items = itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(
                "item", "description", true, PageRequest.of(0, 20));
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0), equalTo(item));
    }

    @Test
    void shouldFindNoneByNameOrDescription() {
        List<Item> emptyList = itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(
                        "text", "text1", true, PageRequest.of(0, 20));
        assertThat(emptyList.size(), equalTo(0));
    }
}
