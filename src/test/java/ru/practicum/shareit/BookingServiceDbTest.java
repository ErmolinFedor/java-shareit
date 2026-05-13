package ru.practicum.shareit;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.exeption.ConflictException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceDbTest {

  private final BookingService bookingService;
  private final UserService userService;
  private final ItemService itemService;

  private UserDto owner;
  private UserDto booker;
  private ItemDto itemDto;

  @BeforeEach
  void setUp() throws ValidationException {
    String uniqueSuffix = java.util.UUID.randomUUID().toString().substring(0, 8);

    owner =
        userService.create(
            UserDto.builder().name("Owner").email("owner" + uniqueSuffix + "@mail.com").build());

    booker =
        userService.create(
            UserDto.builder().name("Booker").email("booker" + uniqueSuffix + "@mail.com").build());

    itemDto =
        itemService.addNewItem(
            owner.getId(),
            ItemDto.builder().name("Drill").description("Powerful").available(true).build());
  }

  @Test
  void createBookingShouldSaveWithWaitingStatus() throws ValidationException {
    BookingInputDto input = new BookingInputDto();
    input.setItemId(itemDto.getId());
    input.setStart(LocalDateTime.now().plusDays(1));
    input.setEnd(LocalDateTime.now().plusDays(2));

    BookingDto saved = bookingService.create(booker.getId(), input);

    assertNotNull(saved.getId());
    assertEquals(Status.WAITING, saved.getStatus());
    assertEquals(booker.getId(), saved.getBooker().getId());
  }

  @Test
  void createBookingByOwner_ShouldThrowNotFound() {
    BookingInputDto input = new BookingInputDto();
    input.setItemId(itemDto.getId());
    input.setStart(LocalDateTime.now().plusDays(1));
    input.setEnd(LocalDateTime.now().plusDays(2));

    assertThrows(ConflictException.class, () -> bookingService.create(owner.getId(), input));
  }

  @Test
  void approveBookingShouldChangeStatus() throws ValidationException {
    BookingInputDto input = new BookingInputDto();
    input.setItemId(itemDto.getId());
    input.setStart(LocalDateTime.now().plusDays(1));
    input.setEnd(LocalDateTime.now().plusDays(2));

    BookingDto booking = bookingService.create(booker.getId(), input);

    BookingDto approved = bookingService.approve(owner.getId(), booking.getId(), true);

    assertEquals(Status.APPROVED, approved.getStatus());
  }

  @Test
  void getAllByBookerWithStateFuture_ShouldReturnBookings() throws ValidationException {
    BookingInputDto input = new BookingInputDto();
    input.setItemId(itemDto.getId());
    input.setStart(LocalDateTime.now().plusDays(5));
    input.setEnd(LocalDateTime.now().plusDays(10));
    bookingService.create(booker.getId(), input);

    List<BookingDto> futureBookings = bookingService.getAllByBooker(booker.getId(), "FUTURE");

    assertFalse(futureBookings.isEmpty());
    assertTrue(futureBookings.getFirst().getStart().isAfter(LocalDateTime.now()));
  }
}
