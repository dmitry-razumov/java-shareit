package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
public class CommentDto {
    private Long id;
    @NotBlank
    private String text;
//    private Long itemId;
    private String authorName;
    private LocalDateTime created;
    ItemInCommentField item;
}
