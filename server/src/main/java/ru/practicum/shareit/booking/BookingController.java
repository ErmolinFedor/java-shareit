package ru.practicum.shareit.booking;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.exeption.ValidationException;

/** TODO Sprint add-bookings. */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
  private final BookingService bookingService;

  private static final String USER_ID_HEADER = "X-Sharer-User-Id";

  @PostMapping
  public BookingDto create(
      @RequestHeader(USER_ID_HEADER) Integer userId, @RequestBody BookingInputDto bookingInputDto)
      throws ValidationException {
    log.info("POST /bookings | userId: {}", userId);
    return bookingService.create(userId, bookingInputDto);
  }

  @PatchMapping("/{bookingId}")
  public BookingDto approve(
      @RequestHeader(USER_ID_HEADER) Integer userId,
      @PathVariable Integer bookingId,
      @RequestParam Boolean approved)
      throws ValidationException {
    log.info("PATCH /bookings/{} | userId: {}, approved: {}", bookingId, userId, approved);
    return bookingService.approve(userId, bookingId, approved);
  }

  @GetMapping("/{bookingId}")
  public BookingDto getById(
      @RequestHeader(USER_ID_HEADER) Integer userId, @PathVariable Integer bookingId) {
    log.info("GET /bookings/{} | userId: {}", bookingId, userId);
    return bookingService.getById(userId, bookingId);
  }

  @GetMapping
  public List<BookingDto> getAllByBooker(
      @RequestHeader(USER_ID_HEADER) Integer userId,
      @RequestParam(defaultValue = "ALL") String state)
      throws ValidationException {
    log.info("GET /bookings?state={} | userId: {}", state, userId);
    return bookingService.getAllByBooker(userId, state);
  }

  @GetMapping("/owner")
  public List<BookingDto> getAllByOwner(
      @RequestHeader(USER_ID_HEADER) Integer userId,
      @RequestParam(defaultValue = "ALL") String state)
      throws ValidationException {
    log.info("GET /bookings/owner?state={} | userId: {}", state, userId);
    return bookingService.getAllByOwner(userId, state);
  }
}
