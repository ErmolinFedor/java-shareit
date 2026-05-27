package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;

class BaseClientTest {

  private RestTemplateBuilder builder;
  private RestTemplate restTemplate;

  @BeforeEach
  void setUp() {
    restTemplate = Mockito.mock(RestTemplate.class);
    builder = Mockito.mock(RestTemplateBuilder.class, Mockito.RETURNS_SELF);
    Mockito.when(builder.build()).thenReturn(restTemplate);
  }

  @Test
  void testAllClientsSuccessScenario() {
    ResponseEntity<Object> successResponse = new ResponseEntity<>(HttpStatus.OK);
    Mockito.when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class),
        anyMap())).thenReturn(successResponse);
    Mockito.when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
        .thenReturn(successResponse);

    String serverUrl = "http://localhost:9090";

    UserClient userClient = new UserClient(serverUrl, builder);
    UserDto userDto = UserDto.builder().build();
    assertNotNull(userClient.createUser(userDto));
    assertNotNull(userClient.updateUser(1L, userDto));
    assertNotNull(userClient.getUser(1L));
    assertNotNull(userClient.getUsers());
    assertNotNull(userClient.deleteUser(1L));

    ItemClient itemClient = new ItemClient(serverUrl, builder);
    ItemDto itemDto = new ItemDto();
    assertNotNull(itemClient.createItem(1L, itemDto));
    assertNotNull(itemClient.updateItem(1L, 1L, itemDto));
    assertNotNull(itemClient.getItem(1L, 1L));
    assertNotNull(itemClient.searchItems(1L, "text"));
    assertNotNull(itemClient.getOwnerItems(1L));
    assertNotNull(itemClient.createComment(1L, 1L, new CommentDto()));

    ItemRequestClient requestClient = new ItemRequestClient(serverUrl, builder);
    assertNotNull(requestClient.createRequest(1L, new ItemRequestDto()));
    assertNotNull(requestClient.getAllRequests(1L));

    BookingClient bookingClient = new BookingClient(serverUrl, builder);
    assertNotNull(bookingClient.bookItem(1L, new BookItemRequestDto()));
    assertNotNull(bookingClient.getBooking(1L, 1L));
    assertNotNull(bookingClient.getBookings(1L, BookingState.ALL, 0, 10));
    assertNotNull(bookingClient.getOwnerBookings(1L, BookingState.ALL, 0, 10));
    assertNotNull(bookingClient.approveBooking(1L, 1L, true));
  }

  @Test
  void testBaseClientExceptionScenario() {
    String serverUrl = "http://localhost:9090";
    UserClient userClient = new UserClient(serverUrl, builder);

    ResponseEntity<Object> badResponse = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    Mockito.when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class),
        anyMap())).thenReturn(badResponse);
    Mockito.when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
        .thenReturn(badResponse);

    assertNotNull(userClient.getUser(1L));

    ResponseEntity<Object> serverErrorResponse = new ResponseEntity<>(
        HttpStatus.INTERNAL_SERVER_ERROR);
    Mockito.when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class),
        anyMap())).thenReturn(serverErrorResponse);

    assertNotNull(userClient.getUser(1L));

    try {
      userClient.getUsers();
    } catch (Exception ignored) {
    }
  }
}
