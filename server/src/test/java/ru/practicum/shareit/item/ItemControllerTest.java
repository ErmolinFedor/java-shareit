package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private ItemService itemService;

  private ItemDto itemDto;
  private CommentDto commentDto;
  private static final String USER_HEADER = "X-Sharer-User-Id";

  @BeforeEach
  void setUp() {
    itemDto = ItemDto.builder()
        .id(1)
        .name("Отвертка")
        .description("Крестовая, магнитная")
        .available(true)
        .comments(Collections.emptyList())
        .build();

    commentDto = CommentDto.builder()
        .id(1)
        .text("Отличный инструмент, помог!")
        .authorName("Алексей")
        .created(LocalDateTime.now())
        .build();
  }

  @Test
  void createItemShouldReturn200AndItem() throws Exception {
    when(itemService.addNewItem(anyInt(), any(ItemDto.class))).thenReturn(itemDto);

    mvc.perform(post("/items")
            .header(USER_HEADER, 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(itemDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(itemDto.getId()))
        .andExpect(jsonPath("$.name").value(itemDto.getName()));
  }

  @Test
  void updateItemShouldReturn200AndUpdated() throws Exception {
    ItemDto updateData = ItemDto.builder().name("Супер Отвертка").build();
    ItemDto updated = ItemDto.builder().id(1).name("Супер Отвертка").available(true).build();

    when(itemService.updateItem(anyInt(), anyInt(), any(ItemDto.class))).thenReturn(updated);

    mvc.perform(patch("/items/{itemId}", 1)
            .header(USER_HEADER, 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(updateData)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Супер Отвертка"));
  }

  @Test
  void getItemShouldReturnItemWithStatus200() throws Exception {
    when(itemService.getItemById(anyInt(), anyInt())).thenReturn(itemDto);

    mvc.perform(get("/items/{itemId}", 1)
            .header(USER_HEADER, 1))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(itemDto.getId()))
        .andExpect(jsonPath("$.description").value(itemDto.getDescription()));
  }

  @Test
  void getOwnerItemsShouldReturnList() throws Exception {
    when(itemService.getOwnerItems(anyInt())).thenReturn(List.of(itemDto));

    mvc.perform(get("/items")
            .header(USER_HEADER, 1))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(itemDto.getId()));
  }

  @Test
  void searchItemsShouldReturnMatchedItems() throws Exception {
    when(itemService.searchItems(anyString())).thenReturn(List.of(itemDto));

    mvc.perform(get("/items/search")
            .param("text", "отвертка"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].name").value(itemDto.getName()));
  }

  @Test
  void addCommentShouldReturnSavedComment() throws Exception {
    when(itemService.addComment(anyInt(), anyInt(), any(CommentDto.class))).thenReturn(commentDto);

    mvc.perform(post("/items/{itemId}/comment", 1)
            .header(USER_HEADER, 2)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(commentDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(commentDto.getId()))
        .andExpect(jsonPath("$.text").value(commentDto.getText()))
        .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()));
  }
}
