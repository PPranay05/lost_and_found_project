package com.example.lostfound.service;

import com.example.lostfound.dto.AdminDashboardStatsDto;
import com.example.lostfound.entity.ClaimStatus;
import com.example.lostfound.entity.ItemStatus;
import com.example.lostfound.repository.ClaimRepository;
import com.example.lostfound.repository.FoundItemRepository;
import com.example.lostfound.repository.LostItemRepository;
import com.example.lostfound.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final LostItemRepository lostItemRepository;
    private final FoundItemRepository foundItemRepository;
    private final ClaimRepository claimRepository;

    @Autowired
    public AdminService(UserRepository userRepository,
                        LostItemRepository lostItemRepository,
                        FoundItemRepository foundItemRepository,
                        ClaimRepository claimRepository) {
        this.userRepository = userRepository;
        this.lostItemRepository = lostItemRepository;
        this.foundItemRepository = foundItemRepository;
        this.claimRepository = claimRepository;
    }

    public AdminDashboardStatsDto getDashboardStats() {
        AdminDashboardStatsDto stats = new AdminDashboardStatsDto();
        stats.setTotalUsers(userRepository.count());
        stats.setTotalLostItems(lostItemRepository.count());
        stats.setTotalFoundItems(foundItemRepository.count());
        stats.setPendingClaims(claimRepository.countByStatus(ClaimStatus.PENDING) + claimRepository.countByStatus(ClaimStatus.UNDER_REVIEW));
        
        long matchedLost = lostItemRepository.countByStatus(ItemStatus.MATCHED);
        long matchedFound = foundItemRepository.countByStatus(ItemStatus.MATCHED);
        stats.setMatchedItems(matchedLost + matchedFound);

        long claimedLost = lostItemRepository.countByStatus(ItemStatus.CLAIMED);
        long claimedFound = foundItemRepository.countByStatus(ItemStatus.CLAIMED);
        stats.setClaimedItems(claimedLost + claimedFound);

        long returnedLost = lostItemRepository.countByStatus(ItemStatus.RETURNED);
        long returnedFound = foundItemRepository.countByStatus(ItemStatus.RETURNED);
        stats.setReturnedItems(returnedLost + returnedFound);

        stats.setRejectedClaims(claimRepository.countByStatus(ClaimStatus.REJECTED));

        return stats;
    }
}
