package ru.practicum.shareit.user;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
  private final UserRepository inMemoryUserRepository;

  @Override
  public UserDto create(UserDto userDto) throws ValidationException {
    log.info("Запрос на создание пользователя с email: {}", userDto.getEmail());
    if (inMemoryUserRepository.isEmailExists(userDto.getEmail())) {
      log.warn("Ошибка создания: email {} уже существует", userDto.getEmail());
      throw new ValidationException("Email уже зарегистрирован");
    }
    User user = UserMapper.toUser(userDto);
    UserDto createdUser = UserMapper.toUserDto(inMemoryUserRepository.create(user));
    log.info("Пользователь успешно создан с id: {}", createdUser.getId());
    return createdUser;
  }

  @Override
  public UserDto update(Integer id, UserDto userDto) throws ValidationException {
    log.info("Запрос на обновление пользователя с id: {}. Данные: {}", id, userDto);
    User user = findUserByIdOrThrow(id);

    if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
      if (inMemoryUserRepository.isEmailExists(userDto.getEmail())) {
        log.warn(
            "Не удалось обновить email для пользователя {}: адрес {} занят",
            id,
            userDto.getEmail());
        throw new ValidationException("Email уже зарегистрирован");
      }
      user.setEmail(userDto.getEmail());
    }

    if (userDto.getName() != null && !userDto.getName().isBlank()) {
      user.setName(userDto.getName());
    }

    log.info("Пользователь с id: {} успешно обновлен", id);
    return UserMapper.toUserDto(inMemoryUserRepository.update(user));
  }

  @Override
  public UserDto getById(Integer id) {
    User user = findUserByIdOrThrow(id);
    return UserMapper.toUserDto(user);
  }

  @Override
  public List<UserDto> getAll() {
    log.debug("Получение списка всех пользователей");
    return inMemoryUserRepository.findAll().stream()
        .map(UserMapper::toUserDto)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(Integer id) {
    findUserByIdOrThrow(id);
    log.info("Удаление пользователя с id: {}", id);
    inMemoryUserRepository.delete(id);
  }

  private User findUserByIdOrThrow(Integer id) {
    return inMemoryUserRepository
        .findById(id)
        .orElseThrow(
            () -> {
              log.warn("Пользователь с id {} не найден", id);
              return new NotFoundException("Пользователь с id " + id + " не найден");
            });
  }
}
