package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull(message = "Дата начала аренды не должна быть null")
    @FutureOrPresent(message = "Дата начала аренды должна быть не ранее сегодня")
    @Column(name = "start_date")
    private LocalDateTime start;
    @NotNull(message = "Дата конца аренды не должна быть null")
    @Future(message = "Дата конца аренды должна быть позднее сегодня")
    @Column(name = "end_date")
    private LocalDateTime end;
    @NotNull(message = "item не должна быть null")
    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;
    @NotNull(message = "booker не должен быть null")
    @ManyToOne
    @JoinColumn(name = "booker_id", referencedColumnName = "id")
    private User booker;
    @Enumerated(EnumType.STRING)
    private Status status;
}
