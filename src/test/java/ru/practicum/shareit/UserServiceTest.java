package ru.practicum.shareit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

public abstract class UserServiceTest extends BaseServiceTest {
  protected UserService userService;

  @BeforeEach
  protected abstract void setUp();

  @Test
  void createUserSuccessfully() throws ValidationException {
    UserDto userDto = createValidUserDto();
    UserDto savedUser = userService.create(userDto);

    assertNotNull(savedUser.getId(), "ID не должен быть null после создания");
    assertEquals(userDto.getEmail(), savedUser.getEmail());
    assertEquals(1, userService.getAll().size());
  }

  @Test
  void createAndIncrementIdWithMultipleUsers() throws ValidationException {
    UserDto user1 = createValidUserDto();
    UserDto user2 = createValidUserDto();
    user2.setEmail("petr@yandex.ru");

    UserDto saved1 = userService.create(user1);
    UserDto saved2 = userService.create(user2);

    assertEquals(1, saved1.getId());
    assertEquals(2, saved2.getId());
  }

  @Test
  void createUserWithDuplicateEmailShouldFail() throws ValidationException {
    UserDto user1 = createValidUserDto();
    UserDto user2 = createValidUserDto();

    userService.create(user1);

    assertThrows(
        ValidationException.class,
        () -> userService.create(user2),
        "Должно быть выброшено исключение при дублировании email");
  }

  @Test
  void updateUserNameSuccessfully() throws ValidationException {
    UserDto user = userService.create(createValidUserDto());
    UserDto updateData = UserDto.builder().name("New Name").build();

    UserDto updatedUser = userService.update(user.getId(), updateData);

    assertEquals("New Name", updatedUser.getName());
    assertEquals(user.getEmail(), updatedUser.getEmail(), "Email не должен измениться");
  }
}
