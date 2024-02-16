package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemInRequestField {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private long requestId;
}
