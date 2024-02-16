package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.repository.CommentRepository;
import java.time.LocalDateTime;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    TestEntityManager em;
    @Autowired
    CommentRepository commentRepository;

    private User owner;
    private User booker;
    private Item item;
    private Comment comment;

    static User createOwner() {
        return User.builder()
                .name("Bob")
                .email("user@user.com")
                .build();
    }

    static User createBooker() {
        return User.builder()
                .name("Tim")
                .email("booker@user.com")
                .build();
    }

    static Item createItem(User owner) {
        return Item.builder()
                .name("pen")
                .description("smth")
                .available(true)
                .owner(owner)
                .build();
    }

    static Comment createComment(User booker, Item item) {
        return Comment.builder()
                .item(item)
                .created(LocalDateTime.now())
                .author(booker)
                .text("text")
                .build();
    }

    @BeforeEach
    void beforeEach() {
        owner = createOwner();
        booker = createBooker();
        item = createItem(owner);
        comment = createComment(booker, item);
        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        em.persist(comment);
    }

    @Test
    void shouldFindAllByItemId() {
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        assertThat(comments.size(), equalTo(1));
        assertThat(comments.get(0), equalTo(comment));
    }

    @Test
    void shouldFindNoneAllByNoExistItemId() {
        List<Comment> emptyList = commentRepository.findAllByItemId(99L);
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void shouldFindAllByItemIds() {
        List<Comment> comments = commentRepository.findAllByItemIds(List.of(item.getId()));
        assertThat(comments.size(), equalTo(1));
        assertThat(comments.get(0), equalTo(comment));
    }

    @Test
    void shouldFindNoneAllByNoExistItemIds() {
        List<Comment> emptyList = commentRepository.findAllByItemIds(List.of(99L));
        assertThat(emptyList.size(), equalTo(0));
    }
}
