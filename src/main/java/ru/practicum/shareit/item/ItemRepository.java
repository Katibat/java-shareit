package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, QueryByExampleExecutor<Item> {
    List<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    List<Item> findByRequestId(Long requestId);

    @Query("SELECT i FROM Item AS i " +
            "WHERE i.available = true AND " +
            "(upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')))")
    List<Item> searchItemsByTextInNameAndDescription(String text, Pageable pageable);

    Item findItemByRequestId(Long itemId);
}
