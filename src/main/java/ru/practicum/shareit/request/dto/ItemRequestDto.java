package ru.practicum.shareit.request.dto;

import java.util.Date;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

/** TODO Sprint add-item-requests. */
@Data
@Builder
public class ItemRequestDto {
  private Integer id;
  private String description;
  private User requestor;
  private Date created;
}
