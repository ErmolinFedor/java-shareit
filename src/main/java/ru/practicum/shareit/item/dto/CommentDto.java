package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentDto {
  private Integer id;

  @NotBlank(message = "Текст комментария не может быть пустым")
  private String text;

  private String authorName;

  private LocalDateTime created;
}
