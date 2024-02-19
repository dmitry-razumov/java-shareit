package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class CommentDto {
    private long id;
    @NotBlank (message = "text не должен быть пробелом, пустым или null")
    @Size(max = 512, message = "text не должен быть больше 512 символов")
    private String text;
}
