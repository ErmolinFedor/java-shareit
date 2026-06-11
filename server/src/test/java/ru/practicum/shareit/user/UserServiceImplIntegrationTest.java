package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class UserServiceImplIntegrationTest {

  @Autowired
  private UserService userService;

  @Test
  void createAndGetUserIntegrationCheck() throws ValidationException {
    UserDto dto = UserDto.builder()
        .name("Интеграционный Юзер")
        .email("integration@shareit.ru")
        .build();

    UserDto saved = userService.create(dto);
    assertNotNull(saved.getId());
    assertEquals(dto.getName(), saved.getName());

    UserDto found = userService.getById(saved.getId());
    assertNotNull(found);
    assertEquals(saved.getEmail(), found.getEmail());
  }
}
