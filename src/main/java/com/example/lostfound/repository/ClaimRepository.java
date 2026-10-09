package com.example.lostfound.repository;

import com.example.lostfound.entity.Claim;
import com.example.lostfound.entity.ClaimStatus;
import com.example.lostfound.entity.FoundItem;
import com.example.lostfound.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    Optional<Claim> findByClaimIdCode(String claimIdCode);
    List<Claim> findByClaimant(User claimant);
    List<Claim> findByFoundItem(FoundItem foundItem);
    List<Claim> findByStatus(ClaimStatus status);
    boolean existsByClaimantAndFoundItemAndStatusNot(User claimant, FoundItem foundItem, ClaimStatus status);
    long countByStatus(ClaimStatus status);
}
