package ru.practicum.shareit.item;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exeption.AccessDeniedException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
  private final ItemRepository itemRepository;
  private final UserRepository userRepository;
  private final CommentRepository commentRepository;
  private final BookingRepository bookingRepository;

  @Override
  @Transactional
  public ItemDto addNewItem(Integer userId, ItemDto itemDto) {
    log.info("Запрос на добавление вещи пользователем {}: {}", userId, itemDto.getName());

    User owner = findUserOrThrow(userId);

    Item item = ItemMapper.toItem(itemDto, owner);
    Item createdItem = itemRepository.create(item);

    log.info("Вещь успешно добавлена с id: {}", createdItem.getId());
    return ItemMapper.toItemDto(createdItem);
  }

  @Override
  @Transactional
  public ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto) {
    log.info("Запрос на обновление вещи id: {} пользователем id: {}", itemId, userId);

    Item item = getItemOrThrow(itemId);

    if (!item.getOwner().getId().equals(userId)) {
      throw new AccessDeniedException("Только владелец может редактировать вещь");
    }

    if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
      item.setName(itemDto.getName());
    }
    if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
      item.setDescription(itemDto.getDescription());
    }
    if (itemDto.getAvailable() != null) {
      item.setAvailable(itemDto.getAvailable());
    }

    log.info("Вещь id: {} успешно обновлена", itemId);
    return ItemMapper.toItemDto(itemRepository.update(item));
  }

  @Override
  public ItemDto getItemById(Integer itemId, Integer userId) {
    Item item = getItemOrThrow(itemId);
    ItemDto dto = ItemMapper.toItemDto(item);

    dto.setComments(
        commentRepository.findAllByItemId(itemId).stream()
            .map(CommentMapper::toCommentDto)
            .collect(Collectors.toList()));

    if (item.getOwner().getId().equals(userId)) {
      LocalDateTime now = LocalDateTime.now();

      Booking last =
          bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(
              itemId, Status.APPROVED, now);

      Booking next =
          bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
              itemId, Status.APPROVED, now);

      if (last != null) {
        dto.setLastBooking(new ItemDto.BookingShortDto(last.getId(), last.getBooker().getId()));
      }
      if (next != null) {
        dto.setNextBooking(new ItemDto.BookingShortDto(next.getId(), next.getBooker().getId()));
      }
    }

    return dto;
  }

  @Override
  public List<ItemDto> getOwnerItems(Integer userId) {
    log.info("Получение списка вещей владельца id: {}", userId);

    List<Item> items = itemRepository.findAllByOwnerId(userId);

    List<Booking> allBookings = bookingRepository.findAllByItemInAndStatus(items, Status.APPROVED);

    List<Comment> allComments =
        commentRepository.findAllByItemIdIn(
            items.stream().map(Item::getId).collect(Collectors.toList()));

    LocalDateTime now = LocalDateTime.now();

    return items.stream()
        .map(
            item -> {
              ItemDto dto = ItemMapper.toItemDto(item);

              List<Booking> itemBookings =
                  allBookings.stream()
                      .filter(b -> b.getItem().getId().equals(item.getId()))
                      .toList();

              Booking last =
                  itemBookings.stream()
                      .filter(b -> b.getStart().isBefore(now))
                      .max(Comparator.comparing(Booking::getStart))
                      .orElse(null);

              Booking next =
                  itemBookings.stream()
                      .filter(b -> b.getStart().isAfter(now))
                      .min(Comparator.comparing(Booking::getStart))
                      .orElse(null);

              if (last != null) {
                dto.setLastBooking(
                    new ItemDto.BookingShortDto(last.getId(), last.getBooker().getId()));
              }
              if (next != null) {
                dto.setNextBooking(
                    new ItemDto.BookingShortDto(next.getId(), next.getBooker().getId()));
              }

              dto.setComments(
                  allComments.stream()
                      .filter(c -> c.getItem().getId().equals(item.getId()))
                      .map(CommentMapper::toCommentDto)
                      .collect(Collectors.toList()));

              return dto;
            })
        .sorted(Comparator.comparing(ItemDto::getId))
        .collect(Collectors.toList());
  }

  @Override
  public List<ItemDto> searchItems(String text) {
    if (text == null || text.isBlank()) {
      log.debug("Пустой поисковый запрос, возвращаем пустой список");
      return Collections.emptyList();
    }
    log.debug("Поиск вещей в БД по запросу: '{}'", text);
    return itemRepository.search(text).stream()
        .map(ItemMapper::toItemDto)
        .collect(Collectors.toList());
  }

  @Transactional
  @Override
  public CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto)
      throws ValidationException {
    boolean hasBooking =
        bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
            userId, itemId, Status.APPROVED, LocalDateTime.now());

    if (!hasBooking) {
      throw new ValidationException(
          "Вы не можете оставить отзыв: аренда не найдена или еще не завершена");
    }

    User author = findUserOrThrow(userId);
    Item item = getItemOrThrow(itemId);

    Comment comment = CommentMapper.toComment(commentDto, item, author);
    comment.setCreated(LocalDateTime.now());

    return CommentMapper.toCommentDto(commentRepository.save(comment));
  }

  private Item getItemOrThrow(Integer itemId) {
    return itemRepository
        .findById(itemId)
        .orElseThrow(() -> new NotFoundException(String.format("Вещь с id %s не найдена", itemId)));
  }

  private User findUserOrThrow(Integer userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(
            () -> new NotFoundException(String.format("Пользователь с id %s не найден", userId)));
  }
}
