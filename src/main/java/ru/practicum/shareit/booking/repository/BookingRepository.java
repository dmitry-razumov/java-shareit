package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.Status;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long userId, Status status);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id in (" +
            "select i.id " +
            "from Item as i " +
            "where i.owner.id = :userId) " +
            "order by b.start desc ")
    List<Booking> findAllByOwnerIdOrderByStartDesc(@Param("userId") long userId);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id in (" +
            "select i.id " +
            "from Item as i " +
            "where i.owner.id = :userId) " +
            "and b.end < :end " +
            "order by b.start desc ")
    List<Booking> findAllByOwnerIdAndEndBeforeOrderByStartDesc(
            @Param("userId")long userId, @Param("end")LocalDateTime end);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id in (" +
            "select i.id " +
            "from Item as i " +
            "where i.owner.id = :userId) " +
            "and b.start < :start and b.end > :end " +
            "order by b.start desc ")
    List<Booking> findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            @Param("userId") long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id in (" +
            "select i.id " +
            "from Item as i " +
            "where i.owner.id = :userId) " +
            "and b.start > :start " +
            "order by b.start desc ")
    List<Booking> findAllByOwnerIdAndStartAfterOrderByStartDesc(
            @Param("userId") long userId, @Param("start") LocalDateTime start);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id in (" +
            "select i.id " +
            "from Item as i " +
            "where i.owner.id = ?1) " +
            "and b.status = ?2 " +
            "order by b.start desc ")
    List<Booking> findAllByOwnerIdAndStatusOrderByStartDesc(
            @Param("userId") long userId, @Param("status") Status status);

    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByStartDescItemIdDesc(
            long id, Status status, LocalDateTime start);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
            long itemId, Status status, LocalDateTime start);

    List<Booking>  findAllByBookerIdAndItemIdAndEndBefore(long bookerId, long itemId, LocalDateTime end);
}
