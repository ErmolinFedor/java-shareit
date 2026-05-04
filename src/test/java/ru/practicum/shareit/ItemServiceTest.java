package ru.practicum.shareit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exeption.AccessDeniedException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

public abstract class ItemServiceTest extends BaseServiceTest {

  protected ItemService itemService;
  protected UserService userService;

  @BeforeEach
  protected abstract void setUp();

  protected ItemDto createValidItemDto() {
    return ItemDto.builder().name("Дрель").description("Мощная дрель").available(true).build();
  }

  @Test
  void addNewItemSuccessfully() throws ValidationException {
    UserDto owner = userService.create(createValidUserDto());
    ItemDto itemDto = createValidItemDto();

    ItemDto savedItem = itemService.addNewItem(owner.getId(), itemDto);

    assertNotNull(savedItem.getId());
    assertEquals(itemDto.getName(), savedItem.getName());
    assertEquals(owner.getId(), savedItem.getOwnerId());
  }

  @Test
  void addNewItemWithNonExistentUserShouldFail() {
    ItemDto itemDto = createValidItemDto();

    assertThrows(
        NotFoundException.class,
        () -> itemService.addNewItem(99, itemDto),
        "Должно быть выброшено исключение, если пользователя не существует");
  }

  @Test
  void updateItemByOwnerSuccessfully() throws AccessDeniedException, ValidationException {
    UserDto owner = userService.create(createValidUserDto());
    ItemDto savedItem = itemService.addNewItem(owner.getId(), createValidItemDto());

    ItemDto updateData = ItemDto.builder().name("Дрель новая").available(false).build();

    ItemDto updated = itemService.updateItem(owner.getId(), savedItem.getId(), updateData);

    assertEquals("Дрель новая", updated.getName());
    assertFalse(updated.getAvailable());
    assertEquals(
        savedItem.getDescription(), updated.getDescription(), "Описание не должно измениться");
  }

  @Test
  void updateItemByNonOwnerShouldFail() throws ValidationException {
    UserDto owner = userService.create(createValidUserDto());
    UserDto stranger =
        userService.create(UserDto.builder().name("Stranger").email("str@mail.com").build());
    ItemDto savedItem = itemService.addNewItem(owner.getId(), createValidItemDto());

    ItemDto updateData = ItemDto.builder().name("Украденная вещь").build();

    assertThrows(
        AccessDeniedException.class,
        () -> itemService.updateItem(stranger.getId(), savedItem.getId(), updateData),
        "Только владелец может редактировать вещь");
  }

  @Test
  void searchItemsShouldReturnOnlyAvailable() throws ValidationException {
    UserDto owner = userService.create(createValidUserDto());

    ItemDto item1 = createValidItemDto();
    item1.setName("Дрель");

    ItemDto item2 = createValidItemDto();
    item2.setName("Дрель старая");
    item2.setAvailable(false);

    itemService.addNewItem(owner.getId(), item1);
    itemService.addNewItem(owner.getId(), item2);

    List<ItemDto> result = itemService.searchItems("дрель");

    assertEquals(1, result.size());
    assertEquals("Дрель", result.get(0).getName());
  }

  @Test
  void searchWithEmptyTextShouldReturnEmptyList() throws ValidationException {
    UserDto owner = userService.create(createValidUserDto());
    itemService.addNewItem(owner.getId(), createValidItemDto());

    List<ItemDto> result = itemService.searchItems("");

    assertTrue(result.isEmpty(), "При пустом поиске должен вернуться пустой список");
  }
}
