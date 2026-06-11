package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import java.util.List;

public interface ItemRequestService {

  ItemRequestResponseDto create(Integer userId, ItemRequestDto dto);

  List<ItemRequestResponseDto> getUserRequests(Integer userId);

  List<ItemRequestResponseDto> getAllRequests(Integer userId);

  ItemRequestResponseDto getById(Integer userId, Integer requestId);
}
