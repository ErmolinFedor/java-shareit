package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ItemRequestClient itemRequestClient;

  @Test
  void testRequestEndpoints() throws Exception {
    Mockito.when(itemRequestClient.createRequest(anyLong(), any()))
        .thenReturn(ResponseEntity.ok().build());
    Mockito.when(itemRequestClient.getAllRequests(anyLong()))
        .thenReturn(ResponseEntity.ok().build());

    ItemRequestDto dto = new ItemRequestDto();
    dto.setDescription("Description");

    mockMvc.perform(
        post("/requests").header("X-Sharer-User-Id", 1L).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk());

    mockMvc.perform(get("/requests").header("X-Sharer-User-Id", 1L)).andExpect(status().isOk());

    mockMvc.perform(get("/requests/all").header("X-Sharer-User-Id", 1L)).andExpect(status().isOk());

    mockMvc.perform(
            get("/requests/all").header("X-Sharer-User-Id", 1L).param("from", "0").param("size", "10"))
        .andExpect(status().isOk());

    mockMvc.perform(get("/requests/1").header("X-Sharer-User-Id", 1L)).andExpect(status().isOk());
  }
}
