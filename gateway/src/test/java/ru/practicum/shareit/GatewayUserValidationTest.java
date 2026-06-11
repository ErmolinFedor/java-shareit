package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class GatewayUserValidationTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private UserClient userClient;

  @Test
  void createUserWithInvalidEmailShouldReturn400BadRequest() throws Exception {
    UserDto badUser = UserDto.builder()
        .name("Невалидный Юзер")
        .email("it-is-not-email!")
        .build();

    mvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(badUser)))
        .andExpect(status().isBadRequest());
  }
}
