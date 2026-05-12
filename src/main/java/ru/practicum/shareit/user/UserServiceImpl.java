package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.ConflictException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  @Override
  @Transactional
  public UserDto create(UserDto userDto) throws ValidationException {
    log.info("Запрос на создание пользователя с email: {}", userDto.getEmail());

    if (userRepository.isEmailExists(userDto.getEmail())) {
      throw new ConflictException(
          String.format("Email %s уже зарегистрирован", userDto.getEmail()));
    }

    User user = UserMapper.toUser(userDto);
    User createdUser = userRepository.create(user);

    log.info("Пользователь успешно создан с id: {}", createdUser.getId());
    return UserMapper.toUserDto(createdUser);
  }

  @Override
  @Transactional
  public UserDto update(Integer id, UserDto userDto) {
    log.info("Запрос на обновление пользователя с id: {}. Данные: {}", id, userDto);

    User user = findUserByIdOrThrow(id);

    if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
      if (userRepository.isEmailExists(userDto.getEmail())) {
        throw new ConflictException(
            String.format(
                "Не удалось обновить email для пользователя %s: адрес %s занят",
                id, userDto.getEmail()));
      }
      user.setEmail(userDto.getEmail());
    }

    if (userDto.getName() != null && !userDto.getName().isBlank()) {
      user.setName(userDto.getName());
    }

    log.info("Пользователь с id: {} успешно обновлен", id);
    return UserMapper.toUserDto(userRepository.update(user));
  }

  @Override
  public UserDto getById(Integer id) {
    return UserMapper.toUserDto(findUserByIdOrThrow(id));
  }

  @Override
  public List<UserDto> getAll() {
    log.debug("Получение списка всех пользователей");
    return userRepository.findAll().stream()
        .map(UserMapper::toUserDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void delete(Integer id) {
    findUserByIdOrThrow(id);
    log.info("Удаление пользователя с id: {}", id);
    userRepository.delete(id);
  }

  private User findUserByIdOrThrow(Integer id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
  }
}
