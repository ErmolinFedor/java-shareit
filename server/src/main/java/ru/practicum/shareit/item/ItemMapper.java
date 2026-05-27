package ru.practicum.shareit.item;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {
  public static ItemDto toItemDto(Item item) {
    List<CommentDto> commentDtos = Collections.emptyList();

    if (item.getComments() != null && !item.getComments().isEmpty()) {
      commentDtos = item.getComments().stream()
          .map(CommentMapper::toCommentDto)
          .collect(Collectors.toList());
    }

    return ItemDto.builder()
        .id(item.getId())
        .name(item.getName())
        .description(item.getDescription())
        .available(item.getAvailable())
        .ownerId(item.getOwner().getId())
        .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
        .comments(commentDtos)
        .build();
  }

  public static Item toItem(ItemDto itemDto, User user) {
    return Item.builder()
        .id(itemDto.getId() != null ? itemDto.getId() : null)
        .owner(user)
        .name(itemDto.getName())
        .description(itemDto.getDescription())
        .available(itemDto.getAvailable())
        .build();
  }
}
