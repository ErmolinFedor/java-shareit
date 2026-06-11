package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRequestServiceImplIntegrationTest {

  @Autowired
  private ItemRequestService itemRequestService;

  @Autowired
  private EntityManager em;

  @Test
  void testItemRequestServiceFullCoverage() {
    User requester = User.builder().name("Проситель").email("requester@mail.ru").build();
    em.persist(requester);
    em.flush();

    ItemRequestDto dto = new ItemRequestDto();
    try {
      dto.getClass().getMethod("setDescription", String.class).invoke(dto, "Нужен перфоратор");
    } catch (Exception ignored) {
    }

    ItemRequestResponseDto savedRequest = itemRequestService.create(requester.getId(), dto);
    assertNotNull(savedRequest.getId());

    List<ItemRequestResponseDto> userRequests = itemRequestService.getUserRequests(
        requester.getId());
    assertFalse(userRequests.isEmpty());

    List<ItemRequestResponseDto> allRequests = itemRequestService.getAllRequests(requester.getId());
    assertNotNull(allRequests);

    ItemRequestResponseDto foundRequest = itemRequestService.getById(requester.getId(),
        savedRequest.getId());
    assertNotNull(foundRequest);

    assertThrows(NotFoundException.class,
        () -> itemRequestService.getById(requester.getId(), -999));

    assertThrows(NotFoundException.class, () -> itemRequestService.getUserRequests(-999));
    assertThrows(NotFoundException.class, () -> itemRequestService.getAllRequests(-999));
  }
}
