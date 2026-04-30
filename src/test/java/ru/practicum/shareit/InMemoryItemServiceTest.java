package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import ru.practicum.shareit.item.InMemoryItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.user.InMemoryUserRepository;
import ru.practicum.shareit.user.UserServiceImpl;

public class InMemoryItemServiceTest extends ItemServiceTest {

  @Override
  @BeforeEach
  protected void setUp() {
    InMemoryUserRepository userRepo = new InMemoryUserRepository();
    InMemoryItemRepository itemRepo = new InMemoryItemRepository();

    userService = new UserServiceImpl(userRepo);
    itemService = new ItemServiceImpl(itemRepo, userRepo);
  }
}
