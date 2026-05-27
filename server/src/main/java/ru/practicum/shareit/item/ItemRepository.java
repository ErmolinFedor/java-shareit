package ru.practicum.shareit.item;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Integer> {

  @Query("select i from Item i left join fetch i.request where i.owner.id = ?1")
  List<Item> findAllByOwnerId(Integer ownerId);

  List<Item> findAllByRequestId(Integer requestId);

  List<Item> findAllByRequestIdIn(List<Integer> requestIds);

  @Query(
      "select i from Item i "
          + "where (upper(i.name) like upper(concat('%', ?1, '%')) "
          + "or upper(i.description) like upper(concat('%', ?1, '%'))) "
          + "and i.available = true")
  List<Item> search(String text);

  default Item create(Item item) {
    return save(item);
  }

  default Item update(Item item) {
    return save(item);
  }
}
