package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

  private final ItemRequestService requestService;
  private static final String USER_ID_HEADER = "X-Sharer-User-Id";

  @PostMapping
  public ItemRequestResponseDto create(@RequestHeader(USER_ID_HEADER) Integer userId,
      @RequestBody ItemRequestDto dto) {
    log.info("Server: POST /requests для userId={}", userId);
    return requestService.create(userId, dto);
  }

  @GetMapping
  public List<ItemRequestResponseDto> getUserRequests(@RequestHeader(USER_ID_HEADER) Integer userId) {
    log.info("Server: GET /requests для userId={}", userId);
    return requestService.getUserRequests(userId);
  }

  @GetMapping("/all")
  public List<ItemRequestResponseDto> getAllRequests(@RequestHeader(USER_ID_HEADER) Integer userId) {
    log.info("Server: GET /requests/all для userId={}", userId);
    return requestService.getAllRequests(userId);
  }

  @GetMapping("/{requestId}")
  public ItemRequestResponseDto getById(@RequestHeader(USER_ID_HEADER) Integer userId,
      @PathVariable Integer requestId) {
    log.info("Server: GET /requests/{} для userId={}", requestId, userId);
    return requestService.getById(userId, requestId);
  }
}