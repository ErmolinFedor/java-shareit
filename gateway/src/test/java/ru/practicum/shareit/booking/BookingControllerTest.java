package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private BookingClient bookingClient;

  @Test
  void testBookingEndpoints() throws Exception {
    Mockito.when(bookingClient.bookItem(anyLong(), any())).thenReturn(ResponseEntity.ok().build());
    Mockito.when(bookingClient.getBooking(anyLong(), anyLong())).thenReturn(ResponseEntity.ok().build());
    Mockito.when(bookingClient.getBookings(anyLong(), any(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().build());
    Mockito.when(bookingClient.getOwnerBookings(anyLong(), any(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().build());

    BookItemRequestDto dto = new BookItemRequestDto();
    dto.setItemId(1L);
    dto.setStart(LocalDateTime.now().plusDays(1));
    dto.setEnd(LocalDateTime.now().plusDays(2));

    mockMvc.perform(post("/bookings").header("X-Sharer-User-Id", 1L)
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk());

    mockMvc.perform(get("/bookings/1").header("X-Sharer-User-Id", 1L))
        .andExpect(status().isOk());

    mockMvc.perform(get("/bookings").header("X-Sharer-User-Id", 1L).param("state", "ALL"))
        .andExpect(status().isOk());

    mockMvc.perform(get("/bookings/owner").header("X-Sharer-User-Id", 1L).param("state", "ALL"))
        .andExpect(status().isOk());

    mockMvc.perform(get("/bookings").header("X-Sharer-User-Id", 1L).param("state", "UNSUPPORTED"))
        .andExpect(status().isBadRequest());
  }
}
