package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private UserService userService;

  private UserDto userDto;

  @BeforeEach
  void setUp() {
    userDto = UserDto.builder()
        .id(1)
        .name("Иван Петров")
        .email("ivan@yandex.ru")
        .build();
  }

  @Test
  void createUserShouldReturn200AndUser() throws Exception {
    when(userService.create(any(UserDto.class))).thenReturn(userDto);

    mvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(userDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userDto.getId()))
        .andExpect(jsonPath("$.name").value(userDto.getName()))
        .andExpect(jsonPath("$.email").value(userDto.getEmail()));
  }

  @Test
  void updateUserShouldReturn200AndUpdatedUser() throws Exception {
    UserDto updateData = UserDto.builder().name("Иван Обновленный").build();
    UserDto updatedUser = UserDto.builder()
        .id(1)
        .name("Иван Обновленный")
        .email("ivan@yandex.ru")
        .build();

    when(userService.update(anyInt(), any(UserDto.class))).thenReturn(updatedUser);

    mvc.perform(patch("/users/{userId}", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(updateData)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(updatedUser.getId()))
        .andExpect(jsonPath("$.name").value(updatedUser.getName()));
  }

  @Test
  void getUserShouldReturnUser() throws Exception {
    when(userService.getById(anyInt())).thenReturn(userDto);

    mvc.perform(get("/users/{userId}", 1))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userDto.getId()))
        .andExpect(jsonPath("$.email").value(userDto.getEmail()));
  }

  @Test
  void getAllUsersShouldReturnList() throws Exception {
    when(userService.getAll()).thenReturn(List.of(userDto));

    mvc.perform(get("/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(userDto.getId()));
  }

  @Test
  void deleteUserShouldReturn200() throws Exception {
    doNothing().when(userService).delete(anyInt());

    mvc.perform(delete("/users/{userId}", 1))
        .andExpect(status().isOk());
  }
}
