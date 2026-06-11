package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@DataJpaTest
public class ItemRepositoryTest {
  @Autowired
  private ItemRepository itemRepository;
  @Autowired
  private TestEntityManager em;

  @Test
  void testSearchSuccess() {
    User owner = new User(null, "Owner", "owner@mail.com");
    em.persist(owner);

    Item item = new Item();
    item.setName("Дрель");
    item.setDescription("Мощная дрель");
    item.setAvailable(true);
    item.setOwner(owner);
    em.persist(item);

    List<Item> result = itemRepository.search("дРЕл");

    assertEquals(1, result.size());
    assertEquals("Дрель", result.get(0).getName());
  }

  @Test
  void testSearchOnlyAvailable() {
    User owner = User.builder()
        .name("Owner")
        .email("owner@mail.com")
        .build();
    em.persist(owner);

    Item item = Item.builder()
        .name("Дрель")
        .description("Мощная")
        .available(false)
        .owner(owner)
        .build();

    em.persist(item);
    em.flush();

    List<Item> result = itemRepository.search("дрель");

    assertTrue(result.isEmpty());
  }

  @Test
  void testFindAllByOwnerIdWithFetch() {
    User owner = User.builder()
        .name("Owner")
        .email("owner@mail.com")
        .build();
    em.persist(owner);

    User requester = User.builder()
        .name("Requester")
        .email("req@mail.com")
        .build();
    em.persist(requester);

    ItemRequest request = ItemRequest.builder()
        .description("Need a drill")
        .requestor(requester)
        .created(LocalDateTime.now())
        .build();
    em.persist(request);

    Item item = Item.builder()
        .name("Дрель")
        .description("Крутая дрель")
        .available(true)
        .owner(owner)
        .request(request)
        .build();
    em.persist(item);

    em.flush();
    em.clear();

    List<Item> items = itemRepository.findAllByOwnerId(owner.getId());

    assertNotNull(items);
    assertEquals(1, items.size());
    assertEquals("Дрель", items.get(0).getName());
    assertEquals(owner.getId(), items.get(0).getOwner().getId());

    assertNotNull(items.get(0).getRequest());
    assertEquals("Need a drill", items.get(0).getRequest().getDescription());
  }

  @Test
  void testCreateAndUpdate() {
    User owner = User.builder()
        .name("User")
        .email("user@mail.com")
        .build();
    em.persist(owner);

    Item item = Item.builder()
        .name("Дрель")
        .description("Мощная")
        .available(true)
        .owner(owner)
        .build();

    Item savedItem = itemRepository.create(item);
    assertNotNull(savedItem.getId());

    savedItem.setName("Супер Дрель");
    Item updatedItem = itemRepository.update(savedItem);

    assertEquals("Супер Дрель", updatedItem.getName());
  }
}
