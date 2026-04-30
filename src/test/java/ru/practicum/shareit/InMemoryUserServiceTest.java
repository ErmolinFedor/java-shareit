package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import ru.practicum.shareit.user.InMemoryUserRepository;
import ru.practicum.shareit.user.UserServiceImpl;

public class InMemoryUserServiceTest extends UserServiceTest {

  @Override
  @BeforeEach
  protected void setUp() {
    InMemoryUserRepository repo = new InMemoryUserRepository();
    userService = new UserServiceImpl(repo);
  }
}
