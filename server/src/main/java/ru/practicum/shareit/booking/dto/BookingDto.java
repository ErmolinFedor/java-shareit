package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

/** TODO Sprint add-bookings. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
  private Integer id;

  private LocalDateTime start;

  private LocalDateTime end;

  private ItemDto item;

  private UserDto booker;

  private Status status;
}
