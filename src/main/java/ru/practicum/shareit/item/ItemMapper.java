package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {
  public static ItemDto toItemDto(Item item) {
    return ItemDto.builder()
        .id(item.getId())
        .name(item.getName())
        .description(item.getDescription())
        .available(item.getAvailable())
        .ownerId(item.getOwner().getId())
        .build();
  }

  public static Item toItem(ItemDto itemDto, User user) {
    return Item.builder()
        .owner(user)
        .name(itemDto.getName())
        .description(itemDto.getDescription())
        .available(itemDto.getAvailable())
        .build();
  }
}
