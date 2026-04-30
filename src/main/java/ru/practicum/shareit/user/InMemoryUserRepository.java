package ru.practicum.shareit.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

@Repository
@Slf4j
public class InMemoryUserRepository implements UserRepository {
  private final Map<Integer, User> users = new HashMap<>();
  private final Set<String> emails = new HashSet<>();
  private int currentId = 1;

  @Override
  public User save(User user) {
    if (user.getId() != null) {
      findById(user.getId()).ifPresent(oldUser -> emails.remove(oldUser.getEmail().toLowerCase()));
    }
    if (user.getId() == null) {
      user.setId(currentId++);
    }
    users.put(user.getId(), user);
    emails.add(user.getEmail().toLowerCase());
    log.debug("Пользователь с id: {} сохранен в памяти", user.getId());
    return user;
  }

  @Override
  public Optional<User> findById(Integer id) {
    log.trace("Поиск пользователя по id: {} в памяти", id);
    return Optional.ofNullable(users.get(id));
  }

  @Override
  public List<User> findAll() {
    log.trace("Запрос всех пользователей из памяти. Текущее количество: {}", users.size());
    return new ArrayList<>(users.values());
  }

  @Override
  public void delete(Integer id) {
    log.debug("Удаление пользователя с id: {} из памяти", id);
    findById(id).ifPresent(user -> emails.remove(user.getEmail().toLowerCase()));
    users.remove(id);
  }

  @Override
  public boolean isEmailExists(String email) {
    log.trace("Проверка существования email: {} в памяти", email);
    return emails.contains(email.toLowerCase());
  }
}
