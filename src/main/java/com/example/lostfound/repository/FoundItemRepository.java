package com.example.lostfound.repository;

import com.example.lostfound.entity.FoundItem;
import com.example.lostfound.entity.ItemStatus;
import com.example.lostfound.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoundItemRepository extends JpaRepository<FoundItem, Long>, JpaSpecificationExecutor<FoundItem> {
    Optional<FoundItem> findByFoundItemIdCode(String foundItemIdCode);
    List<FoundItem> findByFinder(User finder);
    List<FoundItem> findByStatus(ItemStatus status);
    List<FoundItem> findByCategoryIgnoreCase(String category);
    long countByStatus(ItemStatus status);
}
