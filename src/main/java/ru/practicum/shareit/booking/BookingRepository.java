package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

  @EntityGraph(attributePaths = {"item", "booker"})
  List<Booking> findAllByBookerIdOrderByStartDesc(Integer bookerId);

  List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
      Integer bookerId, LocalDateTime now1, LocalDateTime now2);

  List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Integer bookerId, LocalDateTime now);

  List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Integer bookerId, LocalDateTime now);

  List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Integer bookerId, Status status);

  @EntityGraph(attributePaths = {"item", "booker"})
  List<Booking> findAllByItemOwnerIdOrderByStartDesc(Integer ownerId);

  List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
      Integer ownerId, LocalDateTime now1, LocalDateTime now2);

  List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
      Integer ownerId, LocalDateTime now);

  List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
      Integer ownerId, LocalDateTime now);

  List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Integer ownerId, Status status);

  boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(
      Integer bookerId, Integer itemId, Status status, LocalDateTime now);

  Booking findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(
      Integer itemId, Status status, LocalDateTime now);

  Booking findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
      Integer itemId, Status status, LocalDateTime now);

  List<Booking> findAllByItemInAndStatus(List<Item> items, Status status);
}
