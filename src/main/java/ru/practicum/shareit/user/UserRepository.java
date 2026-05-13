package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

  boolean existsByEmail(String email);

  default User create(User user) {
    return save(user);
  }

  default User update(User user) {
    return save(user);
  }

  default void delete(Integer id) {
    deleteById(id);
  }

  default boolean isEmailExists(String email) {
    return existsByEmail(email);
  }
}
