package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerIdOrderByIdAsc(Long ownerId);

    @Query("SELECT i FROM Item i WHERE (i.name ILIKE %:text% OR i.description ILIKE %:text%) AND i.available = true")
    List<Item> findItemsByNameOrDescription(@Param("text") String text);
}