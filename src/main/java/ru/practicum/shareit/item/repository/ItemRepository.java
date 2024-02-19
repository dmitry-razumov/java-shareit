package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.item.model.Item;
import java.util.List;

public interface ItemRepository
        extends JpaRepository<Item, Long>, PagingAndSortingRepository<Item, Long> {
    List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(
            String text, String text1, boolean b, Pageable pageable);

    List<Item> findAllByOwnerIdOrderById(long ownerId, Pageable pageable);
}
