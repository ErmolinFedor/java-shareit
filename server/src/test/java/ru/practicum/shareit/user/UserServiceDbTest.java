package ru.practicum.shareit.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exeption.ConflictException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceDbTest {
  private final UserService userService;

  private UserDto createValidUserDto() {
    return UserDto.builder().name("Ivan").email("ivan@yandex.ru").build();
  }

  @Test
  void createUserSuccessfully() throws ValidationException {
    UserDto userDto = createValidUserDto();
    UserDto savedUser = userService.create(userDto);

    assertNotNull(savedUser.getId());
    assertEquals(userDto.getEmail(), savedUser.getEmail());

    List<UserDto> allUsers = userService.getAll();
    assertTrue(allUsers.contains(savedUser));
  }

  @Test
  void createUserWithDuplicateEmailShouldFail() throws ValidationException {
    UserDto user1 = createValidUserDto();
    UserDto user2 = createValidUserDto();

    userService.create(user1);

    assertThrows(ConflictException.class, () -> userService.create(user2));
  }

  @Test
  void updateUserNameSuccessfully() throws ValidationException {
    UserDto user = userService.create(createValidUserDto());
    UserDto updateData = UserDto.builder().name("New Name").build();

    UserDto updatedUser = userService.update(user.getId(), updateData);

    assertEquals("New Name", updatedUser.getName());
    assertEquals(user.getEmail(), updatedUser.getEmail());
  }

  @Test
  void deleteUserSuccessfully() throws ValidationException {
    UserDto user = userService.create(createValidUserDto());
    userService.delete(user.getId());

    assertThrows(NotFoundException.class, () -> userService.getById(user.getId()));
  }
}
