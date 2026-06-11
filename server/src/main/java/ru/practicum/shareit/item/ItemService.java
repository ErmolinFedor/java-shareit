package ru.practicum.shareit.item;

import java.util.List;
import ru.practicum.shareit.exeption.AccessDeniedException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemService {
  ItemDto addNewItem(Integer userId, ItemDto itemDto);

  ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto) throws AccessDeniedException;

  ItemDto getItemById(Integer itemId, Integer userId);

  List<ItemDto> getOwnerItems(Integer userId);

  List<ItemDto> searchItems(String text);

  CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto)
      throws ValidationException;
}
