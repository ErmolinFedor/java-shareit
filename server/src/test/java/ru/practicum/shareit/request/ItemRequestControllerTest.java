package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private ItemRequestService requestService;

  private ItemRequestResponseDto responseDto;
  private static final String USER_HEADER = "X-Sharer-User-Id";

  @BeforeEach
  void setUp() {
    responseDto = ItemRequestResponseDto.builder()
        .id(1)
        .description("Нужна дрель")
        .created(LocalDateTime.now())
        .items(List.of())
        .build();
  }

  @Test
  void createRequestShouldReturn200() throws Exception {
    ItemRequestDto inputDto = new ItemRequestDto("Нужна дрель");
    when(requestService.create(anyInt(), any(ItemRequestDto.class))).thenReturn(responseDto);

    mvc.perform(post("/requests")
            .header(USER_HEADER, 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(inputDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(responseDto.getId()))
        .andExpect(jsonPath("$.description").value(responseDto.getDescription()));
  }

  @Test
  void getUserRequestsShouldReturnList() throws Exception {
    when(requestService.getUserRequests(anyInt())).thenReturn(List.of(responseDto));

    mvc.perform(get("/requests")
            .header(USER_HEADER, 1))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(responseDto.getId()))
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  void getRequestByIdShouldReturnRequest() throws Exception {
    when(requestService.getById(anyInt(), anyInt())).thenReturn(responseDto);

    mvc.perform(get("/requests/{requestId}", 1L)
            .header(USER_HEADER, 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(responseDto.getId()))
        .andExpect(jsonPath("$.description").value(responseDto.getDescription()));
  }
}
