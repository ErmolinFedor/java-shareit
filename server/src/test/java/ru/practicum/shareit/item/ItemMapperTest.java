package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

public class ItemMapperTest {
  @Test
  void toItemDtoNullRequest() {
    User owner = User.builder().id(1).build();
    Item item = Item.builder()
        .id(1)
        .owner(owner)
        .request(null)
        .build();

    ItemDto dto = ItemMapper.toItemDto(item);

    assertNull(dto.getRequestId());
  }

  @Test
  void toItemDtoWithRequest() {
    User owner = User.builder().id(1).build();
    ItemRequest request = ItemRequest.builder().id(10).build();
    Item item = Item.builder()
        .id(1)
        .name("Дрель")
        .owner(owner) // <--- Добавили
        .request(request)
        .build();

    ItemDto dto = ItemMapper.toItemDto(item);

    assertEquals(10, dto.getRequestId());
    assertEquals("Дрель", dto.getName());
  }

  @Test
  void toItemTest() {
    User owner = User.builder().id(1).build();
    ItemDto dto = ItemDto.builder()
        .name("Молоток")
        .description("Тяжелый")
        .available(true)
        .build();

    Item item = ItemMapper.toItem(dto, owner);

    assertEquals("Молоток", item.getName());
    assertEquals("Тяжелый", item.getDescription());
    assertTrue(item.getAvailable());
  }

  @Test
  void toItemAllFields() {
    User user = User.builder().id(1).name("User").build();
    ItemDto dto = ItemDto.builder().name("Item").description("Desc").available(true).build();

    Item result = ItemMapper.toItem(dto, user);

    assertEquals("Item", result.getName());
    assertEquals(user, result.getOwner());
  }

  @Test
  void toItemDtoWithCommentsList() {
    User user = User.builder().id(1).name("User").build();
    Comment comment = new Comment();
    comment.setId(1);
    comment.setText("Great item!");
    comment.setAuthor(user);
    Item item = Item.builder()
        .id(1)
        .owner(user)
        .comments(List.of(comment))
        .build();

    ItemDto dto = ItemMapper.toItemDto(item);

    assertFalse(dto.getComments().isEmpty());
  }

  @Test
  void toItemFullMapping() {
    User user = User.builder().id(1).name("Owner").build();
    ItemDto dto = ItemDto.builder().name("Hammer").description("Heavy").available(true).build();

    Item result = ItemMapper.toItem(dto, user);

    assertEquals("Hammer", result.getName());
    assertEquals(user, result.getOwner());
  }

  @Test
  void toItemDtoWithRealComments() {
    User author = User.builder().id(2).name("Ivan").build();

    Comment comment = new Comment();
    comment.setId(1);
    comment.setText("Great item!");
    comment.setAuthor(author);

    Item item = Item.builder()
        .id(1)
        .name("Дрель")
        .owner(User.builder().id(1).build())
        .comments(List.of(comment))
        .build();

    ItemDto dto = ItemMapper.toItemDto(item);

    assertNotNull(dto.getComments());
    assertEquals(1, dto.getComments().size());
    assertEquals("Great item!", dto.getComments().get(0).getText());
    assertEquals("Ivan", dto.getComments().get(0).getAuthorName());
  }
}
