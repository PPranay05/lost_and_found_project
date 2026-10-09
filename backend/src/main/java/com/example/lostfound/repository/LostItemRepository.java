package com.example.lostfound.repository;

import com.example.lostfound.entity.ItemStatus;
import com.example.lostfound.entity.LostItem;
import com.example.lostfound.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LostItemRepository extends JpaRepository<LostItem, Long>, JpaSpecificationExecutor<LostItem> {
    Optional<LostItem> findByLostItemIdCode(String lostItemIdCode);
    List<LostItem> findByReporter(User reporter);
    List<LostItem> findByStatus(ItemStatus status);
    List<LostItem> findByCategoryIgnoreCase(String category);
    long countByStatus(ItemStatus status);
}
