package ru.practicum.shareit.request;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
  private final ItemRequestClient requestClient;
  private static final String USER_ID_HEADER = "X-Sharer-User-Id";

  @PostMapping
  public ResponseEntity<Object> createRequest(@RequestHeader(USER_ID_HEADER) long userId,
      @RequestBody @Valid ItemRequestDto requestDto) {
    log.info("Creating item request for userId={}", userId);
    return requestClient.createRequest(userId, requestDto);
  }

  @GetMapping
  public ResponseEntity<Object> getUserRequests(@RequestHeader(USER_ID_HEADER) long userId) {
    log.info("Get item requests for owner userId={}", userId);
    return requestClient.getUserRequests(userId);
  }

  @GetMapping("/all")
  public ResponseEntity<Object> getAllRequests(@RequestHeader(USER_ID_HEADER) long userId) {
    log.info("Get all item requests created by other users for userId={}", userId);
    return requestClient.getAllRequests(userId);
  }

  @GetMapping("/{requestId}")
  public ResponseEntity<Object> getRequestById(@RequestHeader(USER_ID_HEADER) long userId,
      @PathVariable Long requestId) {
    log.info("Get item request id={} for userId={}", requestId, userId);
    return requestClient.getRequestById(userId, requestId);
  }
}
