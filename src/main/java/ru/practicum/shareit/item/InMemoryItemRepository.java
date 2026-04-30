package ru.practicum.shareit.item;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

@Repository
@Slf4j
public class InMemoryItemRepository implements ItemRepository {
  private final Map<Integer, Item> items = new HashMap<>();
  private int currentId = 1;

  @Override
  public Item save(Item item) {
    if (item.getId() == null) {
      item.setId(currentId++);
      log.debug("Генерация нового ID: {} для вещи '{}'", item.getId(), item.getName());
    }
    items.put(item.getId(), item);
    log.debug("Вещь с ID: {} сохранена в памяти", item.getId());
    return item;
  }

  @Override
  public Optional<Item> findById(Integer itemId) {
    log.trace("Запрос вещи по ID: {}", itemId);
    return Optional.ofNullable(items.get(itemId));
  }

  @Override
  public List<Item> findAllByOwnerId(Integer userId) {
    log.trace("Запрос списка всех вещей владельца ID: {}", userId);
    return items.values().stream()
        .filter(item -> item.getOwner().getId().equals(userId))
        .collect(Collectors.toList());
  }

  @Override
  public List<Item> search(String text) {
    if (text.isBlank()) {
      log.trace("Получен пустой запрос для поиска, возвращаем пустой список");
      return Collections.emptyList();
    }
    String query = text.toLowerCase();
    log.trace("Выполнение поиска по тексту: '{}'", query);

    List<Item> result =
        items.values().stream()
            .filter(Item::getAvailable)
            .filter(
                item ->
                    item.getName().toLowerCase().contains(query)
                        || item.getDescription().toLowerCase().contains(query))
            .collect(Collectors.toList());

    log.debug("Поиск завершен. Найдено вещей: {}", result.size());
    return result;
  }
}
