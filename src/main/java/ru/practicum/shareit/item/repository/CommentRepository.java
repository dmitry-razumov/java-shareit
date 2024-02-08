package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItemId(long id);

    @Query("select c from Comment as c where c.item.id in (?1)")
    List<Comment> findAllByItemIds(List<Long> ids);
}
