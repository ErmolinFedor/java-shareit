package ru.practicum.shareit;

import ru.practicum.shareit.user.dto.UserDto;

public abstract class BaseServiceTest {

  protected UserDto createValidUserDto() {
    return UserDto.builder().name("Ivan Ivanov").email("ivan@yandex.ru").build();
  }
}
