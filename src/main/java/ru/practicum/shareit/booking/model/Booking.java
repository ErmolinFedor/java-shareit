package ru.practicum.shareit.booking.model;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

/** TODO Sprint add-bookings. */
@Data
@Builder
public class Booking {
  private Integer id;
  private LocalDate start;
  private LocalDate end;
  private Item item;
  private User booker;
  private Status status;
}
