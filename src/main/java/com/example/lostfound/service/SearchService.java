package com.example.lostfound.service;

import com.example.lostfound.dto.FoundItemDto;
import com.example.lostfound.dto.LostItemDto;
import com.example.lostfound.dto.SearchRequest;
import com.example.lostfound.entity.FoundItem;
import com.example.lostfound.entity.ItemStatus;
import com.example.lostfound.entity.LostItem;
import com.example.lostfound.repository.FoundItemRepository;
import com.example.lostfound.repository.LostItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final LostItemRepository lostItemRepository;
    private final FoundItemRepository foundItemRepository;

    @Autowired
    public SearchService(LostItemRepository lostItemRepository, FoundItemRepository foundItemRepository) {
        this.lostItemRepository = lostItemRepository;
        this.foundItemRepository = foundItemRepository;
    }

    public Map<String, Object> searchItems(SearchRequest request) {
        String type = request.getType() != null ? request.getType().toUpperCase() : "ALL";
        String query = request.getQuery() != null ? request.getQuery().trim().toLowerCase() : "";
        String category = request.getCategory() != null ? request.getCategory().trim() : "";
        String location = request.getLocation() != null ? request.getLocation().trim() : "";
        String statusStr = request.getStatus() != null ? request.getStatus().trim() : "";

        List<LostItemDto> lostResults = new ArrayList<>();
        List<FoundItemDto> foundResults = new ArrayList<>();

        if (!type.equals("FOUND")) {
            List<LostItem> lostItems = lostItemRepository.findAll();
            lostResults = lostItems.stream()
                    .filter(item -> filterLostItem(item, query, category, location, statusStr, request.getDate()))
                    .map(LostItemDto::new)
                    .collect(Collectors.toList());
        }

        if (!type.equals("LOST")) {
            List<FoundItem> foundItems = foundItemRepository.findAll();
            foundResults = foundItems.stream()
                    .filter(item -> filterFoundItem(item, query, category, location, statusStr, request.getDate()))
                    .map(FoundItemDto::new)
                    .collect(Collectors.toList());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("lostItems", lostResults);
        response.put("foundItems", foundResults);
        response.put("totalResults", lostResults.size() + foundResults.size());
        return response;
    }

    private boolean filterLostItem(LostItem item, String query, String category, String location, String statusStr, java.time.LocalDate date) {
        if (!query.isEmpty()) {
            boolean nameMatch = item.getItemName() != null && item.getItemName().toLowerCase().contains(query);
            boolean descMatch = item.getDescription() != null && item.getDescription().toLowerCase().contains(query);
            boolean brandMatch = item.getBrand() != null && item.getBrand().toLowerCase().contains(query);
            boolean codeMatch = item.getLostItemIdCode() != null && item.getLostItemIdCode().toLowerCase().contains(query);
            if (!nameMatch && !descMatch && !brandMatch && !codeMatch) return false;
        }

        if (!category.isEmpty() && !category.equalsIgnoreCase("ALL")) {
            if (item.getCategory() == null || !item.getCategory().equalsIgnoreCase(category)) return false;
        }

        if (!location.isEmpty()) {
            if (item.getLocationLost() == null || !item.getLocationLost().toLowerCase().contains(location.toLowerCase())) return false;
        }

        if (!statusStr.isEmpty() && !statusStr.equalsIgnoreCase("ALL")) {
            if (item.getStatus() == null || !item.getStatus().name().equalsIgnoreCase(statusStr)) return false;
        }

        if (date != null) {
            if (item.getDateLost() == null || !item.getDateLost().equals(date)) return false;
        }

        return true;
    }

    private boolean filterFoundItem(FoundItem item, String query, String category, String location, String statusStr, java.time.LocalDate date) {
        if (!query.isEmpty()) {
            boolean nameMatch = item.getItemName() != null && item.getItemName().toLowerCase().contains(query);
            boolean descMatch = item.getDescription() != null && item.getDescription().toLowerCase().contains(query);
            boolean brandMatch = item.getBrand() != null && item.getBrand().toLowerCase().contains(query);
            boolean codeMatch = item.getFoundItemIdCode() != null && item.getFoundItemIdCode().toLowerCase().contains(query);
            if (!nameMatch && !descMatch && !brandMatch && !codeMatch) return false;
        }

        if (!category.isEmpty() && !category.equalsIgnoreCase("ALL")) {
            if (item.getCategory() == null || !item.getCategory().equalsIgnoreCase(category)) return false;
        }

        if (!location.isEmpty()) {
            if (item.getLocationFound() == null || !item.getLocationFound().toLowerCase().contains(location.toLowerCase())) return false;
        }

        if (!statusStr.isEmpty() && !statusStr.equalsIgnoreCase("ALL")) {
            if (item.getStatus() == null || !item.getStatus().name().equalsIgnoreCase(statusStr)) return false;
        }

        if (date != null) {
            if (item.getDateFound() == null || !item.getDateFound().equals(date)) return false;
        }

        return true;
    }
}
