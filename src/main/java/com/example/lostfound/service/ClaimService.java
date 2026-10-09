package com.example.lostfound.service;

import com.example.lostfound.dto.ClaimRequestDto;
import com.example.lostfound.entity.*;
import com.example.lostfound.exception.InvalidClaimException;
import com.example.lostfound.exception.ResourceNotFoundException;
import com.example.lostfound.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final FoundItemRepository foundItemRepository;
    private final LostItemRepository lostItemRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final ItemStatusHistoryRepository historyRepository;

    @Autowired
    public ClaimService(ClaimRepository claimRepository,
                        FoundItemRepository foundItemRepository,
                        LostItemRepository lostItemRepository,
                        UserRepository userRepository,
                        TransactionService transactionService,
                        ItemStatusHistoryRepository historyRepository) {
        this.claimRepository = claimRepository;
        this.foundItemRepository = foundItemRepository;
        this.lostItemRepository = lostItemRepository;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
        this.historyRepository = historyRepository;
    }

    @Transactional
    public ClaimRequestDto submitClaim(ClaimRequestDto dto, Long claimantUserId) {
        User claimant = userRepository.findById(claimantUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + claimantUserId));

        FoundItem foundItem = foundItemRepository.findById(dto.getFoundItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Found item not found with id: " + dto.getFoundItemId()));

        if (foundItem.getStatus() == ItemStatus.CLAIMED || foundItem.getStatus() == ItemStatus.RETURNED) {
            throw new InvalidClaimException("This item has already been claimed or returned to its owner");
        }

        if (claimRepository.existsByClaimantAndFoundItemAndStatusNot(claimant, foundItem, ClaimStatus.REJECTED)) {
            throw new InvalidClaimException("You already have an active claim submitted for this item");
        }

        LostItem lostItem = null;
        if (dto.getLostItemId() != null) {
            lostItem = lostItemRepository.findById(dto.getLostItemId()).orElse(null);
        }

        String claimCode = "CLM-" + (3000 + (long)(Math.random() * 7000)) + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        Claim claim = new Claim();
        claim.setClaimIdCode(claimCode);
        claim.setFoundItem(foundItem);
        claim.setLostItem(lostItem);
        claim.setClaimant(claimant);
        claim.setClaimantName(dto.getClaimantName() != null ? dto.getClaimantName() : claimant.getFullName());
        claim.setContactDetails(dto.getContactDetails() != null ? dto.getContactDetails() : claimant.getPhoneNumber());
        claim.setItemDescription(dto.getItemDescription());
        claim.setIdentifyingDetails(dto.getIdentifyingDetails());
        claim.setProofDetails(dto.getProofDetails());
        claim.setStatus(ClaimStatus.PENDING);

        Claim savedClaim = claimRepository.save(claim);

        // Update found item status to MATCHED when claim is pending review
        ItemStatus prevItemStatus = foundItem.getStatus();
        if (prevItemStatus == ItemStatus.REPORTED) {
            foundItem.setStatus(ItemStatus.MATCHED);
            foundItemRepository.save(foundItem);
            recordStatusHistory("FOUND", foundItem.getId(), foundItem.getFoundItemIdCode(), prevItemStatus, ItemStatus.MATCHED, claimant, "Status updated to MATCHED due to incoming ownership claim");
        }

        if (lostItem != null && lostItem.getStatus() == ItemStatus.REPORTED) {
            ItemStatus prevLostStatus = lostItem.getStatus();
            lostItem.setStatus(ItemStatus.MATCHED);
            lostItemRepository.save(lostItem);
            recordStatusHistory("LOST", lostItem.getId(), lostItem.getLostItemIdCode(), prevLostStatus, ItemStatus.MATCHED, claimant, "Status updated to MATCHED due to submitted claim");
        }

        transactionService.recordTransaction("FOUND", foundItem.getId(), foundItem.getFoundItemIdCode(), claimant,
                "SUBMIT_CLAIM", prevItemStatus.name(), ItemStatus.MATCHED.name(),
                claimant.getFullName(), "N/A", "PENDING", "Claim submitted by user: " + claimant.getFullName());

        return new ClaimRequestDto(savedClaim);
    }

    public List<ClaimRequestDto> getAllClaims() {
        return claimRepository.findAll().stream()
                .map(ClaimRequestDto::new)
                .collect(Collectors.toList());
    }

    public List<ClaimRequestDto> getClaimsByClaimant(Long claimantUserId) {
        User claimant = userRepository.findById(claimantUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + claimantUserId));
        return claimRepository.findByClaimant(claimant).stream()
                .map(ClaimRequestDto::new)
                .collect(Collectors.toList());
    }

    public ClaimRequestDto getClaimById(Long id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));
        return new ClaimRequestDto(claim);
    }

    @Transactional
    public ClaimRequestDto reviewClaim(Long claimId, ClaimStatus newClaimStatus, String adminNotes, String qrVerificationResult, User admin) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + claimId));

        ClaimStatus prevClaimStatus = claim.getStatus();
        claim.setStatus(newClaimStatus);
        claim.setAdminNotes(adminNotes);
        if (qrVerificationResult != null) {
            claim.setQrVerificationResult(qrVerificationResult);
        }
        claim.setUpdatedAt(LocalDateTime.now());

        FoundItem foundItem = claim.getFoundItem();
        ItemStatus prevFoundStatus = foundItem.getStatus();

        if (newClaimStatus == ClaimStatus.APPROVED) {
            foundItem.setStatus(ItemStatus.CLAIMED);
            foundItemRepository.save(foundItem);
            recordStatusHistory("FOUND", foundItem.getId(), foundItem.getFoundItemIdCode(), prevFoundStatus, ItemStatus.CLAIMED, admin, "Claim approved by admin");

            if (claim.getLostItem() != null) {
                LostItem lost = claim.getLostItem();
                ItemStatus prevLostStatus = lost.getStatus();
                lost.setStatus(ItemStatus.CLAIMED);
                lostItemRepository.save(lost);
                recordStatusHistory("LOST", lost.getId(), lost.getLostItemIdCode(), prevLostStatus, ItemStatus.CLAIMED, admin, "Item marked CLAIMED upon claim approval");
            }
        } else if (newClaimStatus == ClaimStatus.REJECTED) {
            // Check if there are other pending claims
            long activeClaimsCount = claimRepository.findByFoundItem(foundItem).stream()
                    .filter(c -> c.getStatus() == ClaimStatus.PENDING || c.getStatus() == ClaimStatus.UNDER_REVIEW)
                    .count();
            if (activeClaimsCount <= 1) {
                foundItem.setStatus(ItemStatus.REPORTED);
                foundItemRepository.save(foundItem);
                recordStatusHistory("FOUND", foundItem.getId(), foundItem.getFoundItemIdCode(), prevFoundStatus, ItemStatus.REPORTED, admin, "Status reverted to REPORTED after claim rejection");
            }
        }

        Claim saved = claimRepository.save(claim);

        transactionService.recordTransaction("FOUND", foundItem.getId(), foundItem.getFoundItemIdCode(), admin,
                "REVIEW_CLAIM", prevClaimStatus.name(), newClaimStatus.name(),
                admin != null ? admin.getFullName() : "Admin",
                qrVerificationResult != null ? qrVerificationResult : "N/A",
                newClaimStatus.name(), "Claim " + newClaimStatus + ": " + adminNotes);

        return new ClaimRequestDto(saved);
    }

    @Transactional
    public ClaimRequestDto markItemReturned(Long claimId, User admin, String notes) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + claimId));

        if (claim.getStatus() != ClaimStatus.APPROVED) {
            throw new InvalidClaimException("Only approved claims can be marked as returned");
        }

        FoundItem foundItem = claim.getFoundItem();
        ItemStatus prevFoundStatus = foundItem.getStatus();
        foundItem.setStatus(ItemStatus.RETURNED);
        foundItemRepository.save(foundItem);
        recordStatusHistory("FOUND", foundItem.getId(), foundItem.getFoundItemIdCode(), prevFoundStatus, ItemStatus.RETURNED, admin, "Item handed over to claimant and marked RETURNED");

        if (claim.getLostItem() != null) {
            LostItem lost = claim.getLostItem();
            ItemStatus prevLostStatus = lost.getStatus();
            lost.setStatus(ItemStatus.RETURNED);
            lostItemRepository.save(lost);
            recordStatusHistory("LOST", lost.getId(), lost.getLostItemIdCode(), prevLostStatus, ItemStatus.RETURNED, admin, "Item marked RETURNED upon handover");
        }

        claim.setUpdatedAt(LocalDateTime.now());
        claim.setAdminNotes((claim.getAdminNotes() != null ? claim.getAdminNotes() + " | " : "") + "Item physically returned on " + LocalDateTime.now());
        Claim saved = claimRepository.save(claim);

        transactionService.recordTransaction("FOUND", foundItem.getId(), foundItem.getFoundItemIdCode(), admin,
                "MARK_ITEM_RETURNED", prevFoundStatus.name(), ItemStatus.RETURNED.name(),
                admin != null ? admin.getFullName() : "Admin", "QR Verified & Handover Complete", "RETURNED", notes);

        return new ClaimRequestDto(saved);
    }

    private void recordStatusHistory(String itemType, Long itemId, String itemCode, ItemStatus oldStatus, ItemStatus newStatus, User user, String comments) {
        ItemStatusHistory history = new ItemStatusHistory();
        history.setItemType(itemType);
        history.setItemId(itemId);
        history.setItemCode(itemCode);
        history.setPreviousStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedBy(user);
        history.setComments(comments);
        historyRepository.save(history);
    }
}
