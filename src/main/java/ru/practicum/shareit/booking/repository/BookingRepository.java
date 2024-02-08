package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.Status;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long userId,
                                                                             LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long userId, Status status);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id in (" +
            "select i.id " +
            "from Item as i " +
            "where i.owner.id = ?1) " +
            "order by b.start desc ")
    List<Booking> findAllByOwnerIdOrderByStartDesc(long userId);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id in (" +
            "select i.id " +
            "from Item as i " +
            "where i.owner.id = ?1) " +
            "and b.end < ?2 " +
            "order by b.start desc ")
    List<Booking> findAllByOwnerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime end);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id in (" +
            "select i.id " +
            "from Item as i " +
            "where i.owner.id = ?1) " +
            "and b.start < ?2 and b.end > ?3 " +
            "order by b.start desc ")
    List<Booking> findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(long userId,
                                                                            LocalDateTime start, LocalDateTime end);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id in (" +
            "select i.id " +
            "from Item as i " +
            "where i.owner.id = ?1) " +
            "and b.start > ?2 " +
            "order by b.start desc ")
    List<Booking> findAllByOwnerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime start);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id in (" +
            "select i.id " +
            "from Item as i " +
            "where i.owner.id = ?1) " +
            "and b.status = ?2 " +
            "order by b.start desc ")
    List<Booking> findAllByOwnerIdAndStatusOrderByStartDesc(long userId, Status status);

    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByStartDescItemIdDesc(
            long id, Status status, LocalDateTime start);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
            long itemId, Status status, LocalDateTime start);

    List<Booking>  findAllByBookerIdAndItemIdAndEndBefore(long bookerId, long itemId, LocalDateTime end);
}
