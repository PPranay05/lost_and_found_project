package com.example.lostfound.service;

import com.example.lostfound.dto.FoundItemDto;
import com.example.lostfound.dto.LostItemDto;
import com.example.lostfound.dto.MatchResultDto;
import com.example.lostfound.entity.FoundItem;
import com.example.lostfound.entity.LostItem;
import com.example.lostfound.repository.FoundItemRepository;
import com.example.lostfound.repository.LostItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class MatchingService {

    private final LostItemRepository lostItemRepository;
    private final FoundItemRepository foundItemRepository;

    @Autowired
    public MatchingService(LostItemRepository lostItemRepository, FoundItemRepository foundItemRepository) {
        this.lostItemRepository = lostItemRepository;
        this.foundItemRepository = foundItemRepository;
    }

    public List<MatchResultDto> findMatchesForLostItem(Long lostItemId) {
        LostItem lostItem = lostItemRepository.findById(lostItemId)
                .orElseThrow(() -> new com.example.lostfound.exception.ResourceNotFoundException("Lost item not found with id: " + lostItemId));

        List<FoundItem> foundItems = foundItemRepository.findAll();
        List<MatchResultDto> matches = new ArrayList<>();

        for (FoundItem foundItem : foundItems) {
            MatchResultDto result = calculateMatch(lostItem, foundItem);
            if (result.getMatchPercentage() >= 40) {
                matches.add(result);
            }
        }

        matches.sort((a, b) -> Integer.compare(b.getMatchPercentage(), a.getMatchPercentage()));
        return matches;
    }

    public List<MatchResultDto> findMatchesForFoundItem(Long foundItemId) {
        FoundItem foundItem = foundItemRepository.findById(foundItemId)
                .orElseThrow(() -> new com.example.lostfound.exception.ResourceNotFoundException("Found item not found with id: " + foundItemId));

        List<LostItem> lostItems = lostItemRepository.findAll();
        List<MatchResultDto> matches = new ArrayList<>();

        for (LostItem lostItem : lostItems) {
            MatchResultDto result = calculateMatch(lostItem, foundItem);
            if (result.getMatchPercentage() >= 40) {
                matches.add(result);
            }
        }

        matches.sort((a, b) -> Integer.compare(b.getMatchPercentage(), a.getMatchPercentage()));
        return matches;
    }

    public List<MatchResultDto> getAllPotentialMatches() {
        List<LostItem> lostItems = lostItemRepository.findAll();
        List<FoundItem> foundItems = foundItemRepository.findAll();
        List<MatchResultDto> allMatches = new ArrayList<>();

        for (LostItem lost : lostItems) {
            for (FoundItem found : foundItems) {
                MatchResultDto match = calculateMatch(lost, found);
                if (match.getMatchPercentage() >= 50) {
                    allMatches.add(match);
                }
            }
        }

        allMatches.sort((a, b) -> Integer.compare(b.getMatchPercentage(), a.getMatchPercentage()));
        return allMatches;
    }

    public MatchResultDto calculateMatch(LostItem lost, FoundItem found) {
        int score = 0;
        List<String> reasons = new ArrayList<>();

        // Category match (35 pts)
        if (lost.getCategory() != null && found.getCategory() != null
                && lost.getCategory().equalsIgnoreCase(found.getCategory())) {
            score += 35;
            reasons.add("Category matches ('" + lost.getCategory() + "')");
        }

        // Name match (25 pts max)
        String lostName = lost.getItemName().toLowerCase();
        String foundName = found.getItemName().toLowerCase();
        if (lostName.equalsIgnoreCase(foundName)) {
            score += 25;
            reasons.add("Item title exact match");
        } else {
            Set<String> lostWords = new HashSet<>(Arrays.asList(lostName.split("\\s+")));
            Set<String> foundWords = new HashSet<>(Arrays.asList(foundName.split("\\s+")));
            lostWords.retainAll(foundWords);
            if (!lostWords.isEmpty()) {
                score += 18;
                reasons.add("Title keyword overlap (" + String.join(", ", lostWords) + ")");
            }
        }

        // Location match (15 pts)
        if (lost.getLocationLost() != null && found.getLocationFound() != null) {
            String lLoc = lost.getLocationLost().toLowerCase();
            String fLoc = found.getLocationFound().toLowerCase();
            if (lLoc.equalsIgnoreCase(fLoc) || lLoc.contains(fLoc) || fLoc.contains(lLoc)) {
                score += 15;
                reasons.add("Location similarity ('" + lost.getLocationLost() + "' vs '" + found.getLocationFound() + "')");
            }
        }

        // Date proximity (10 pts)
        if (lost.getDateLost() != null && found.getDateFound() != null) {
            long daysApart = Math.abs(ChronoUnit.DAYS.between(lost.getDateLost(), found.getDateFound()));
            if (daysApart <= 3) {
                score += 10;
                reasons.add("Date close within " + daysApart + " days");
            } else if (daysApart <= 7) {
                score += 5;
                reasons.add("Date within 7 days");
            }
        }

        // Color match (8 pts)
        if (lost.getColor() != null && found.getColor() != null
                && !lost.getColor().isBlank() && !found.getColor().isBlank()) {
            if (lost.getColor().equalsIgnoreCase(found.getColor()) ||
                lost.getColor().toLowerCase().contains(found.getColor().toLowerCase()) ||
                found.getColor().toLowerCase().contains(lost.getColor().toLowerCase())) {
                score += 8;
                reasons.add("Color match ('" + lost.getColor() + "')");
            }
        }

        // Brand match (7 pts)
        if (lost.getBrand() != null && found.getBrand() != null
                && !lost.getBrand().isBlank() && !found.getBrand().isBlank()) {
            if (lost.getBrand().equalsIgnoreCase(found.getBrand())) {
                score += 7;
                reasons.add("Brand match ('" + lost.getBrand() + "')");
            }
        }

        int finalScore = Math.min(score, 100);
        String reasonStr = reasons.isEmpty() ? "Low overall similarity" : String.join("; ", reasons);

        return new MatchResultDto(new LostItemDto(lost), new FoundItemDto(found), finalScore, reasonStr);
    }
}
