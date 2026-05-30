package ru.practicum.shareit.request;

import java.time.LocalDateTime;
import java.util.List;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.model.User;

public class ItemRequestMapper {

  public static ItemRequestResponseDto toItemRequestResponseDto(ItemRequest request, List<Item> items) {
    List<ItemAnswerDto> itemAnswers = items.stream()
        .map(i -> new ItemAnswerDto(i.getId(), i.getName(), i.getOwner().getId()))
        .toList();

    return ItemRequestResponseDto.builder()
        .id(request.getId())
        .description(request.getDescription())
        .created(request.getCreated())
        .items(itemAnswers)
        .build();
  }

  public static ItemRequest toItemRequest(ItemRequestDto dto, User requestor) {
    return ItemRequest.builder()
        .description(dto.getDescription())
        .requestor(requestor)
        .created(LocalDateTime.now())
        .build();
  }
}
