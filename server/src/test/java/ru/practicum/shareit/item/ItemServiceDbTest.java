package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceDbTest {

  @Autowired
  private ItemService itemService;
  @Autowired
  private UserService userService;
  @Autowired
  private BookingService bookingService;

  private UserDto owner;
  private UserDto booker;
  private ItemDto itemDto;

  @BeforeEach
  void setUp() throws ValidationException {
    UserDto ownerDto = UserDto.builder()
        .name("Владелец")
        .email("owner_db_test@mail.ru")
        .build();
    owner = userService.create(ownerDto);

    UserDto bookerDto = UserDto.builder()
        .name("Арендатор")
        .email("booker_db_test@mail.ru")
        .build();
    booker = userService.create(bookerDto);

    itemDto = ItemDto.builder()
        .name("Дрель")
        .description("Ударная")
        .available(true)
        .build();
    itemDto = itemService.addNewItem(owner.getId(), itemDto);
  }

  @Test
  void addNewItemShouldSaveAndReturnItem() {
    ItemDto savedItem = itemService.addNewItem(owner.getId(), itemDto);

    assertNotNull(savedItem.getId());
    assertEquals(itemDto.getName(), savedItem.getName());
  }

  @Test
  void getItemByIdShouldShowBookingsOnlyForOwner() throws ValidationException, InterruptedException {
    ItemDto savedItem = itemService.addNewItem(owner.getId(), itemDto);

    BookingInputDto bookingInput = new BookingInputDto();
    bookingInput.setItemId(savedItem.getId());

    bookingInput.setStart(LocalDateTime.now().plusSeconds(1));
    bookingInput.setEnd(LocalDateTime.now().plusSeconds(2));

    var booking = bookingService.create(booker.getId(), bookingInput);
    bookingService.approve(owner.getId(), booking.getId(), true);

    Thread.sleep(1500);

    ItemDto ownerView = itemService.getItemById(savedItem.getId(), owner.getId());
    assertNotNull(ownerView.getLastBooking());
    assertEquals(booker.getId(), ownerView.getLastBooking().getBookerId());

    ItemDto lookerView = itemService.getItemById(savedItem.getId(), booker.getId());
    assertNull(lookerView.getLastBooking());
  }

  @Test
  void addCommentShouldSaveCommentAfterBooking() throws Exception {
    ItemDto savedItem = itemService.addNewItem(owner.getId(), itemDto);

    BookingInputDto bookingInput = new BookingInputDto();
    bookingInput.setItemId(savedItem.getId());
    bookingInput.setStart(LocalDateTime.now().plusSeconds(1));
    bookingInput.setEnd(LocalDateTime.now().plusSeconds(2));

    var booking = bookingService.create(booker.getId(), bookingInput);
    bookingService.approve(owner.getId(), booking.getId(), true);

    Thread.sleep(1500);

    CommentDto commentDto = CommentDto.builder().text("Excellent tool!").build();
    CommentDto savedComment = itemService.addComment(booker.getId(), savedItem.getId(), commentDto);

    assertNotNull(savedComment.getId());
    assertEquals("Excellent tool!", savedComment.getText());
    assertEquals(booker.getName(), savedComment.getAuthorName());
  }

  @Test
  void searchItemsShouldFindAvailableItems() {
    String uniqueText = "UniqueSearchText" + System.currentTimeMillis();

    itemService.addNewItem(
        owner.getId(),
        ItemDto.builder().name("Item1").description(uniqueText).available(true).build());
    itemService.addNewItem(
        owner.getId(),
        ItemDto.builder().name("Item2").description(uniqueText).available(true).build());

    List<ItemDto> found = itemService.searchItems(uniqueText);

    assertEquals(2, found.size());
  }
}
