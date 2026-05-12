package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentDto {
  private Integer id;
  private String text;
  private String authorName;
  private LocalDateTime created;
}
