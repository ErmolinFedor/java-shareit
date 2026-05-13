package ru.practicum.shareit.booking;

import static java.util.Objects.isNull;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exeption.AccessDeniedException;
import ru.practicum.shareit.exeption.ConflictException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
  private final BookingRepository bookingRepository;
  private final UserRepository userRepository;
  private final ItemRepository itemRepository;

  @Override
  @Transactional
  public BookingDto create(Integer userId, BookingInputDto dto) throws ValidationException {
    User booker = findUserOrThrow(userId);

    Item item =
        itemRepository
            .findById(dto.getItemId())
            .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

    if (!item.getAvailable()) {
      throw new ConflictException("Вещь недоступна для бронирования");
    }
    if (item.getOwner().getId().equals(userId)) {
      throw new ConflictException("Владелец не может забронировать свою вещь");
    }
    if (isNull(dto.getStart()) || isNull(dto.getEnd()) || dto.getEnd().isBefore(dto.getStart())
        || dto.getEnd().isEqual(dto.getStart())) {
      throw new ConflictException("Дата окончания не может быть раньше или равна дате начала");
    }

    Booking booking = BookingMapper.toBooking(dto, item, booker);
    return BookingMapper.toBookingDto(bookingRepository.save(booking));
  }

  @Override
  @Transactional
  public BookingDto approve(Integer userId, Integer bookingId, Boolean approved)
      throws ValidationException {
    Booking booking = findBookingOrThrow(bookingId);

    if (!booking.getItem().getOwner().getId().equals(userId)) {
      throw new ConflictException("Подтвердить бронирование может только владелец вещи");
    }

    if (booking.getStatus() != Status.WAITING) {
      throw new ValidationException("Статус уже изменен");
    }

    booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
    return BookingMapper.toBookingDto(booking);
  }

  @Override
  public BookingDto getById(Integer userId, Integer bookingId) {
    Booking booking = findBookingOrThrow(bookingId);

    boolean isBooker = booking.getBooker().getId().equals(userId);
    boolean isOwner = booking.getItem().getOwner().getId().equals(userId);

    if (!isBooker && !isOwner) {
      throw new AccessDeniedException("Просмотр доступен только автору или владельцу");
    }

    return BookingMapper.toBookingDto(booking);
  }

  @Override
  public List<BookingDto> getAllByBooker(Integer userId, String stateStr)
      throws ValidationException {
    findUserOrThrow(userId);

    State state =
        State.from(stateStr)
            .orElseThrow(() -> new ValidationException("Unknown state: " + stateStr));

    LocalDateTime now = LocalDateTime.now();
    List<Booking> bookings =
        switch (state) {
          case CURRENT ->
              bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                  userId, now, now);
          case PAST -> bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
          case FUTURE ->
              bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
          case WAITING ->
              bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
          case REJECTED ->
              bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
          default -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
        };

    return bookings.stream()
        .map(BookingMapper::toBookingDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<BookingDto> getAllByOwner(Integer userId, String stateStr)
      throws ValidationException {
    findUserOrThrow(userId);

    if (itemRepository.findAllByOwnerId(userId).isEmpty()) {
      return Collections.emptyList();
    }

    LocalDateTime now = LocalDateTime.now();
    List<Booking> bookings;

    State state =
        State.from(stateStr)
            .orElseThrow(() -> new ValidationException("Не известный state: " + stateStr));

    bookings =
        switch (state) {
          case CURRENT ->
              bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                  userId, now, now);
          case PAST ->
              bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now);
          case FUTURE ->
              bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now);
          case WAITING ->
              bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                  userId, Status.WAITING);
          case REJECTED ->
              bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                  userId, Status.REJECTED);
          default -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
        };

    return bookings.stream()
        .map(BookingMapper::toBookingDto)
        .collect(Collectors.toList());
  }

  private User findUserOrThrow(Integer userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(
            () -> new NotFoundException(String.format("Пользователь с id %s не найден", userId)));
  }

  private Booking findBookingOrThrow(Integer bookingId) {
    return bookingRepository
        .findById(bookingId)
        .orElseThrow(
            () ->
                new NotFoundException(String.format("Бронирование с id %s не найдено", bookingId)));
  }
}
