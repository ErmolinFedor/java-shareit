package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {

  public static BookingDto toBookingDto(Booking booking) {
    return BookingDto.builder()
        .id(booking.getId())
        .start(booking.getStart())
        .end(booking.getEnd())
        .item(ItemMapper.toItemDto(booking.getItem()))
        .booker(UserMapper.toUserDto(booking.getBooker()))
        .status(booking.getStatus())
        .build();
  }

  public static Booking toBooking(BookingInputDto bookingInputDto, Item item, User booker) {
    return Booking.builder()
        .start(bookingInputDto.getStart())
        .end(bookingInputDto.getEnd())
        .item(item)
        .booker(booker)
        .status(Status.WAITING)
        .build();
  }
}
