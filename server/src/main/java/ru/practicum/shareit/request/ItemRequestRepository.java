package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequesterId(long requesterId, Sort sortBy);

    List<ItemRequest> findByRequesterIdNot(long requesterId, Pageable pageable);
}
