package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingInItemField lastBooking;
    private BookingInItemField nextBooking;
    private List<CommentDto> comments;
}
