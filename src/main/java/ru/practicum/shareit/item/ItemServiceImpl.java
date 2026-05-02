package ru.practicum.shareit.item;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.AccessDeniedException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
  private final ItemRepository inMemoryItemRepository;
  private final UserRepository inMemoryUserRepository;

  @Override
  public ItemDto addNewItem(Integer userId, ItemDto itemDto) {
    log.info("Запрос на добавление вещи пользователем {}: {}", userId, itemDto.getName());

    User owner = findUserOrThrow(userId);

    Item createdItem = inMemoryItemRepository.create(ItemMapper.toItem(itemDto, owner));
    log.info("Вещь успешно добавлена с id: {}", createdItem.getId());
    return ItemMapper.toItemDto(createdItem);
  }

  @Override
  public ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto)
      throws AccessDeniedException {
    log.info("Запрос на обновление вещи id: {} пользователем id: {}", itemId, userId);

    Item item = getItemOrThrow(itemId);

    if (!item.getOwner().getId().equals(userId)) {
      throw new AccessDeniedException("Только пользователь может редактировать вещь");
    }

    if (itemDto.getName() != null && !itemDto.getName().isBlank()) item.setName(itemDto.getName());
    if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
      item.setDescription(itemDto.getDescription());
    }
    if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());

    log.info("Вещь id: {} успешно обновлена", itemId);
    return ItemMapper.toItemDto(inMemoryItemRepository.update(item));
  }

  @Override
  public ItemDto getItemById(Integer itemId) {
    Item item = getItemOrThrow(itemId);
    return ItemMapper.toItemDto(item);
  }

  @Override
  public List<ItemDto> getOwnerItems(Integer userId) {
    findUserOrThrow(userId);
    return inMemoryItemRepository.findAllByOwnerId(userId).stream()
        .map(ItemMapper::toItemDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<ItemDto> searchItems(String text) {
    log.debug("Поиск вещей по запросу: '{}'", text);
    return inMemoryItemRepository.search(text).stream()
        .map(ItemMapper::toItemDto)
        .collect(Collectors.toList());
  }

  private Item getItemOrThrow(Integer itemId) {
    return inMemoryItemRepository
        .findById(itemId)
        .orElseThrow(
            () -> {
              log.warn("Вещь с id {} не найдена", itemId);
              return new NotFoundException(String.format("Вещь с id %s не найдена", itemId));
            });
  }

  private User findUserOrThrow(Integer userId) {
    return inMemoryUserRepository
        .findById(userId)
        .orElseThrow(
            () -> {
              log.warn("Ошибка пользователь {} не найден", userId);
              return new NotFoundException(String.format("Пользователь с id %s не найден", userId));
            });
  }
}
