package ru.practicum.shareit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ShareItTests {

  private final UserController userController;
  private final ItemController itemController;

  @Test
  void testCreateUserAndAddItem() throws ValidationException {
    UserDto userDto = UserDto.builder().name("Owner").email("owner@mail.com").build();
    UserDto savedUser = userController.create(userDto);

    ItemDto itemDto =
        ItemDto.builder().name("Hammer").description("Heavy hammer").available(true).build();

    ItemDto savedItem = itemController.create(savedUser.getId(), itemDto);

    assertNotNull(savedItem.getId());
    assertEquals(savedUser.getId(), itemController.getItem(savedItem.getId()).getOwnerId());
  }
}
