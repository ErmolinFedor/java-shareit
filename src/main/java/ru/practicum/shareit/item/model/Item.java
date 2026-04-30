package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

/** TODO Sprint add-controllers. */
@Data
@Builder
public class Item {

  private Integer id;

  @NotBlank(message = "Имя не может быть пустым")
  private String name;

  @NotBlank(message = "Описание не может быть пустым")
  private String description;

  @NotBlank(message = "Статус не может быть пустым")
  private Boolean available;

  @NotBlank(message = "владелец не может быть пустым")
  private User owner;

  private ItemRequest request;
}
