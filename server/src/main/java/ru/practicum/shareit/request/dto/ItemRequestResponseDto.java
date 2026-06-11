package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemRequestResponseDto {
  private Integer id;
  private String description;
  private LocalDateTime created;
  private List<ItemAnswerDto> items;
}
