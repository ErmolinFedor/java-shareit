package ru.practicum.shareit.booking;

import java.util.List;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.exeption.ValidationException;

public interface BookingService {
  BookingDto create(Integer userId, BookingInputDto bookingInputDto) throws ValidationException;

  BookingDto approve(Integer userId, Integer bookingId, Boolean approved)
      throws ValidationException;

  BookingDto getById(Integer userId, Integer bookingId);

  List<BookingDto> getAllByBooker(Integer userId, String state) throws ValidationException;

  List<BookingDto> getAllByOwner(Integer userId, String state) throws ValidationException;
}
