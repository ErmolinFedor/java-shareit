package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository {
  Item save(Item item);

  Optional<Item> findById(Integer itemId);

  List<Item> findAllByOwnerId(Integer userId);

  List<Item> search(String text);
}
