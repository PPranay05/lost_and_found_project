package com.example.lostfound.repository;

import com.example.lostfound.entity.ItemStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemStatusHistoryRepository extends JpaRepository<ItemStatusHistory, Long> {
    List<ItemStatusHistory> findByItemTypeAndItemId(String itemType, Long itemId);
    List<ItemStatusHistory> findByItemCode(String itemCode);
}
