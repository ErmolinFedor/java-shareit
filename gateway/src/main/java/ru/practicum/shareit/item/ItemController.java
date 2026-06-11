package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
  private final ItemClient itemClient;
  private static final String USER_ID_HEADER = "X-Sharer-User-Id";

  @GetMapping
  public ResponseEntity<Object> getOwnerItems(@RequestHeader(USER_ID_HEADER) long userId) {
    log.info("Get items for owner userId={}", userId);
    return itemClient.getOwnerItems(userId);
  }

  @GetMapping("/{itemId}")
  public ResponseEntity<Object> getItem(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable long itemId) {
    log.info("Get item id={} for userId={}", itemId, userId);
    return itemClient.getItem(userId, itemId);
  }

  @PostMapping
  public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) long userId,
      @RequestBody @Valid ItemDto itemDto) {
    log.info("Creating item {} for userId={}", itemDto, userId);
    return itemClient.createItem(userId, itemDto);
  }

  @PatchMapping("/{itemId}")
  public ResponseEntity<Object> patchItem(@RequestHeader(USER_ID_HEADER) long userId,
      @PathVariable long itemId,
      @RequestBody Object itemDto) {
    log.info("Patching item id={} for userId={}, data={}", itemId, userId, itemDto);
    return itemClient.updateItem(userId, itemId, itemDto);
  }

  @GetMapping("/search")
  public ResponseEntity<Object> searchItems(@RequestHeader(USER_ID_HEADER) long userId,
      @RequestParam String text) {
    log.info("Search items by text='{}' for userId={}", text, userId);
    return itemClient.searchItems(userId, text);
  }

  @PostMapping("/{itemId}/comment")
  public ResponseEntity<Object> createComment(@RequestHeader(USER_ID_HEADER) long userId,
      @PathVariable long itemId,
      @RequestBody @Valid CommentDto commentDto) {
    log.info("Creating comment for item id={} by userId={}", itemId, userId);
    return itemClient.createComment(userId, itemId, commentDto);
  }
}