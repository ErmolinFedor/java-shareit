package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

/** TODO Sprint add-controllers. */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
  private final UserService userService;

  @PostMapping
  public UserDto create(@Valid @RequestBody UserDto userDto) throws ValidationException {
    log.info("Получен запрос POST /users с телом: {}", userDto);
    UserDto createdUser = userService.create(userDto);
    log.info(
        "Запрос POST /users успешно обработан. Создан пользователь с id: {}", createdUser.getId());
    return createdUser;
  }

  @PatchMapping("/{id}")
  public UserDto update(@PathVariable Integer id, @RequestBody UserDto userDto)
      throws ValidationException {
    log.info("Получен запрос PATCH /users/{} с телом: {}", id, userDto);
    UserDto updatedUser = userService.update(id, userDto);
    log.info("Запрос PATCH /users/{} успешно выполнен", id);
    return updatedUser;
  }

  @GetMapping("/{id}")
  public UserDto getById(@PathVariable Integer id) {
    log.info("Получен запрос GET /users/{}", id);
    return userService.getById(id);
  }

  @GetMapping
  public List<UserDto> getAll() {
    log.info("Получен запрос GET /users на получение всех пользователей");
    return userService.getAll();
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Integer id) {
    log.info("Получен запрос DELETE /users/{}", id);
    userService.delete(id);
    log.info("Пользователь с id: {} удален", id);
  }
}
