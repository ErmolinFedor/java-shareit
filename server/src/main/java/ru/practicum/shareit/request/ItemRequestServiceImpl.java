package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

  private final ItemRequestRepository requestRepository;
  private final UserRepository userRepository;
  private final ItemRepository itemRepository;

  @Override
  @Transactional
  public ItemRequestResponseDto create(Integer userId, ItemRequestDto dto) {
    log.info("Создание запроса для userId={}", userId);
    User requestor = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("Пользователь не найден с id=" + userId));

    ItemRequest request = ItemRequestMapper.toItemRequest(dto, requestor);

    return ItemRequestMapper.toItemRequestResponseDto(requestRepository.save(request),
        Collections.emptyList());
  }

  @Override
  public List<ItemRequestResponseDto> getUserRequests(Integer userId) {
    log.info("Получение личных запросов для userId={}", userId);
    checkUserExists(userId);
    List<ItemRequest> requests = requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
    return addAnswersToRequests(requests);
  }

  @Override
  public List<ItemRequestResponseDto> getAllRequests(Integer userId) {
    log.info("Получение чужих запросов для userId={}", userId);
    checkUserExists(userId);
    List<ItemRequest> requests = requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(
        userId);
    return addAnswersToRequests(requests);
  }

  @Override
  public ItemRequestResponseDto getById(Integer userId, Integer requestId) {
    log.info("Получение запроса requestId={} для userId={}", requestId, userId);

    Integer sUserId = userId;
    Integer sRequestId = requestId;

    checkUserExists(sUserId);

    ItemRequest request = requestRepository.findById(sRequestId)
        .orElseThrow(() -> new NotFoundException("Запрос не найден с id=" + requestId));

    List<Item> answers = itemRepository.findAllByRequestId(request.getId());
    log.info("Для реального запроса id={} найдено вещей-ответов: {}", request.getId(),
        answers.size());

    return ItemRequestMapper.toItemRequestResponseDto(request, answers);
  }

  private void checkUserExists(Integer userId) {
    if (!userRepository.existsById(userId)) {
      throw new NotFoundException("Пользователь не найден с id=" + userId);
    }
  }

  private List<ItemRequestResponseDto> addAnswersToRequests(List<ItemRequest> requests) {
    if (requests.isEmpty()) {
      return Collections.emptyList();
    }

    List<Integer> requestIds = requests.stream().map(ItemRequest::getId).toList();
    List<Item> allAnswers = itemRepository.findAllByRequestIdIn(requestIds);

    Map<Integer, List<Item>> answersByRequestId = allAnswers.stream()
        .filter(item -> item.getRequest() != null)
        .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

    return requests.stream().map(r -> ItemRequestMapper.toItemRequestResponseDto(r,
        answersByRequestId.getOrDefault(r.getId(), Collections.emptyList()))).toList();
  }
}
