package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private BookingService bookingService;

  private BookingDto bookingDto;
  private BookingInputDto requestDto;
  private static final String USER_HEADER = "X-Sharer-User-Id";

  @BeforeEach
  void setUp() {
    LocalDateTime start = LocalDateTime.now().plusDays(1);
    LocalDateTime end = LocalDateTime.now().plusDays(2);

    requestDto = new BookingInputDto(1, start, end);

    UserDto booker = UserDto.builder().id(2).name("Арендатор").build();
    ItemDto item = ItemDto.builder().id(1).name("Дрель").build();

    bookingDto = BookingDto.builder()
        .id(1)
        .start(start)
        .end(end)
        .status(Status.WAITING)
        .booker(booker)
        .item(item)
        .build();
  }

  @Test
  void createBooking_shouldReturn200AndBooking() throws Exception {
    when(bookingService.create(anyInt(), any(BookingInputDto.class))).thenReturn(bookingDto);

    mvc.perform(post("/bookings")
            .header(USER_HEADER, 2)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(bookingDto.getId()))
        .andExpect(jsonPath("$.status").value("WAITING"));
  }

  @Test
  void approveBooking_shouldReturn200AndApprovedStatus() throws Exception {
    bookingDto.setStatus(Status.APPROVED);
    when(bookingService.approve(anyInt(), anyInt(), anyBoolean())).thenReturn(bookingDto);

    mvc.perform(patch("/bookings/{bookingId}", 1)
            .header(USER_HEADER, 1)
            .param("approved", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("APPROVED"));
  }

  @Test
  void getBooking_shouldReturnBookingDto() throws Exception {
    when(bookingService.getById(anyInt(), anyInt())).thenReturn(bookingDto);

    mvc.perform(get("/bookings/{bookingId}", 1)
            .header(USER_HEADER, 1))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(bookingDto.getId()));
  }

  @Test
  void getBookingsShouldReturnBookerList() throws Exception {
    when(bookingService.getAllByBooker(anyInt(), anyString())).thenReturn(List.of(bookingDto));

    mvc.perform(get("/bookings")
            .header(USER_HEADER, 2)
            .param("state", "ALL"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(bookingDto.getId()));
  }
}
