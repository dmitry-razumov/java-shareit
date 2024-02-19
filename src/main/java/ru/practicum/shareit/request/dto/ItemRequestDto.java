package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {
    private long id;
    @NotBlank (message = "description не должно быть пробелом, пустым или null")
    private String description;
    private LocalDateTime created;
    private List<ItemInRequestField> items;
}
