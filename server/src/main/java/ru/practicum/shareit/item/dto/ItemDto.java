package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** TODO Sprint add-controllers. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
  private Integer id;

  @NotBlank(message = "Имя не может быть пустым")
  private String name;

  @NotBlank(message = "Описание не может быть пустым")
  private String description;

  @NotNull(message = "Статус не может быть пустым")
  private Boolean available;

  private Integer ownerId;

  private Integer requestId;

  private List<CommentDto> comments;

  private BookingShortDto lastBooking;

  private BookingShortDto nextBooking;

  @Data
  @AllArgsConstructor
  public static class BookingShortDto {
    private Integer id;
    private Integer bookerId;
  }
}
