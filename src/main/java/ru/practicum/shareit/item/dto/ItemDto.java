package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/** TODO Sprint add-controllers. */
@Data
@Builder
public class ItemDto {
  private Integer id;

  @NotBlank(message = "Имя не может быть пустым")
  private String name;

  @NotBlank(message = "Описание не может быть пустым")
  private String description;

  @NotNull(message = "Статус не может быть пустым")
  private Boolean available;

  private Integer ownerId;

  private String request;
}
