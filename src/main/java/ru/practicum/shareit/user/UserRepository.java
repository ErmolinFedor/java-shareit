package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;
import ru.practicum.shareit.user.model.User;

public interface UserRepository {
  User save(User user);

  Optional<User> findById(Integer id);

  List<User> findAll();

  void delete(Integer id);

  boolean isEmailExists(String email);
}
