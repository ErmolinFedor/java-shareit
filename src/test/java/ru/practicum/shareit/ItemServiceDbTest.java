package ru.practicum.shareit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceDbTest {

  private final ItemService itemService;
  private final UserService userService;
  private final BookingService bookingService;

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
    itemDto = ItemDto.builder().name("Hammer").description("Heavy").available(true).build();
  }

  @Test
  void addNewItemShouldSaveAndReturnItem() {
    ItemDto savedItem = itemService.addNewItem(owner.getId(), itemDto);

    assertNotNull(savedItem.getId());
    assertEquals(itemDto.getName(), savedItem.getName());
  }

  @Test
  void getItemByIdShouldShowBookingsOnlyForOwner() throws ValidationException {
    ItemDto savedItem = itemService.addNewItem(owner.getId(), itemDto);

    BookingInputDto bookingInput = new BookingInputDto();
    bookingInput.setItemId(savedItem.getId());
    bookingInput.setStart(LocalDateTime.now().minusDays(2));
    bookingInput.setEnd(LocalDateTime.now().minusDays(1));

    var booking = bookingService.create(booker.getId(), bookingInput);
    bookingService.approve(owner.getId(), booking.getId(), true);

    ItemDto ownerView = itemService.getItemById(savedItem.getId(), owner.getId());
    assertNotNull(ownerView.getLastBooking());
    assertEquals(booker.getId(), ownerView.getLastBooking().getBookerId());

    ItemDto lookerView = itemService.getItemById(savedItem.getId(), booker.getId());
    assertNull(lookerView.getLastBooking());
  }

  @Test
  void addCommentShouldSaveCommentAfterBooking() throws ValidationException {
    ItemDto savedItem = itemService.addNewItem(owner.getId(), itemDto);

    BookingInputDto bookingInput = new BookingInputDto();
    bookingInput.setItemId(savedItem.getId());
    bookingInput.setStart(LocalDateTime.now().minusDays(2));
    bookingInput.setEnd(LocalDateTime.now().minusDays(1));

    var booking = bookingService.create(booker.getId(), bookingInput);
    bookingService.approve(owner.getId(), booking.getId(), true);

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
