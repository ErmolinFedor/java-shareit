package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserClient userClient;

  @Test
  void testUserEndpoints() throws Exception {
    Mockito.when(userClient.createUser(any())).thenReturn(ResponseEntity.ok().build());
    Mockito.when(userClient.updateUser(anyLong(), any())).thenReturn(ResponseEntity.ok().build());
    Mockito.when(userClient.getUser(anyLong())).thenReturn(ResponseEntity.ok().build());
    Mockito.when(userClient.getUsers()).thenReturn(ResponseEntity.ok().build());
    Mockito.when(userClient.deleteUser(anyLong())).thenReturn(ResponseEntity.ok().build());

    UserDto dto = UserDto.builder()
        .name("Name")
        .email("test@email.com")
        .build();

    mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk());
    mockMvc.perform(patch("/users/1").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk());
    mockMvc.perform(get("/users/1")).andExpect(status().isOk());
    mockMvc.perform(get("/users")).andExpect(status().isOk());
    mockMvc.perform(delete("/users/1")).andExpect(status().isOk());
  }
}
