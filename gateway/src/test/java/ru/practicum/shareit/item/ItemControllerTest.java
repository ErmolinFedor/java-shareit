package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ItemClient itemClient;

  @Test
  void testAllItemEndpoints() throws Exception {
    Mockito.when(itemClient.createItem(anyLong(), any(ItemDto.class)))
        .thenReturn(ResponseEntity.ok().build());
    Mockito.when(itemClient.updateItem(anyLong(), anyLong(), any()))
        .thenReturn(ResponseEntity.ok().build());
    Mockito.when(itemClient.getItem(anyLong(), anyLong())).thenReturn(ResponseEntity.ok().build());
    Mockito.when(itemClient.searchItems(anyLong(), anyString()))
        .thenReturn(ResponseEntity.ok().build());
    Mockito.when(itemClient.getOwnerItems(anyLong())).thenReturn(ResponseEntity.ok().build());
    Mockito.when(itemClient.createComment(anyLong(), anyLong(), any(CommentDto.class)))
        .thenReturn(ResponseEntity.ok().build());

    ItemDto itemDto = new ItemDto();
    itemDto.setName("Дрель");
    itemDto.setDescription("Описание");
    itemDto.setAvailable(true);

    CommentDto commentDto = new CommentDto();
    try {
      commentDto.getClass().getMethod("setText", String.class)
          .invoke(commentDto, "Отличный комментарий!");
    } catch (Exception ignored) {
    }

    mockMvc.perform(
        post("/items").header("X-Sharer-User-Id", 1L).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(itemDto))).andExpect(status().isOk());

    mockMvc.perform(
        patch("/items/1").header("X-Sharer-User-Id", 1L).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(itemDto))).andExpect(status().isOk());

    mockMvc.perform(get("/items/1").header("X-Sharer-User-Id", 1L)).andExpect(status().isOk());

    mockMvc.perform(get("/items").header("X-Sharer-User-Id", 1L)).andExpect(status().isOk());

    mockMvc.perform(get("/items/search").header("X-Sharer-User-Id", 1L).param("text", "дрель"))
        .andExpect(status().isOk());

    mockMvc.perform(post("/items/1/comment").header("X-Sharer-User-Id", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(commentDto))).andExpect(status().isOk());
  }
}
