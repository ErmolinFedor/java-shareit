package ru.practicum.shareit.item;

import jakarta.validation.Valid;
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
import ru.practicum.shareit.exeption.AccessDeniedException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

/** TODO Sprint add-controllers. */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

  private final ItemService itemService;

  @PostMapping
  public ItemDto create(
      @RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody ItemDto itemDto) {
    log.info("Получен запрос POST /items от пользователя id: {}. Тело: {}", userId, itemDto);
    ItemDto createdItem = itemService.addNewItem(userId, itemDto);
    log.info("Вещь создана с id: {}", createdItem.getId());
    return createdItem;
  }

  @PatchMapping("/{itemId}")
  public ItemDto update(
      @RequestHeader("X-Sharer-User-Id") Integer userId,
      @PathVariable Integer itemId,
      @RequestBody ItemDto itemDto)
      throws AccessDeniedException {
    log.info(
        "Получен запрос PATCH /items/{} от пользователя id: {}. Обновляемые поля: {}",
        itemId,
        userId,
        itemDto);
    return itemService.updateItem(userId, itemId, itemDto);
  }

  @GetMapping("/{itemId}")
  public ItemDto getItem(
      @RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId) {
    log.info("Получен запрос GET /items/{} от пользователя id: {}", itemId, userId);
    return itemService.getItemById(itemId, userId);
  }

  @GetMapping
  public List<ItemDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
    log.info("Получен запрос GET /items от владельца id: {}", userId);
    return itemService.getOwnerItems(userId);
  }

  @GetMapping("/search")
  public List<ItemDto> search(@RequestParam String text) {
    log.info("Получен запрос GET /items/search с текстом: '{}'", text);
    return itemService.searchItems(text);
  }

  @PostMapping("/{itemId}/comment")
  public CommentDto addComment(
      @RequestHeader("X-Sharer-User-Id") Integer userId,
      @PathVariable Integer itemId,
      @Valid @RequestBody CommentDto commentDto)
      throws ValidationException {
    log.info("Получен запрос POST /items/{}/comment от пользователя id: {}", itemId, userId);
    return itemService.addComment(userId, itemId, commentDto);
  }
}
