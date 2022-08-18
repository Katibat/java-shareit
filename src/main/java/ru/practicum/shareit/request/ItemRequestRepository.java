package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
}
