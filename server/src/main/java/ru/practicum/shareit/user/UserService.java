package ru.practicum.shareit.user;

import java.util.List;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
  UserDto create(UserDto userDto) throws ValidationException;

  UserDto update(Integer id, UserDto userDto) throws ValidationException;

  UserDto getById(Integer id);

  List<UserDto> getAll();

  void delete(Integer id);
}
