package com.example.lostfound.service;

import com.example.lostfound.dto.LostItemDto;
import com.example.lostfound.entity.ItemStatus;
import com.example.lostfound.entity.ItemStatusHistory;
import com.example.lostfound.entity.LostItem;
import com.example.lostfound.entity.User;
import com.example.lostfound.exception.ResourceNotFoundException;
import com.example.lostfound.repository.ItemStatusHistoryRepository;
import com.example.lostfound.repository.LostItemRepository;
import com.example.lostfound.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LostItemService {

    private final LostItemRepository lostItemRepository;
    private final UserRepository userRepository;
    private final ItemStatusHistoryRepository historyRepository;
    private final TransactionService transactionService;

    @Autowired
    public LostItemService(LostItemRepository lostItemRepository,
                           UserRepository userRepository,
                           ItemStatusHistoryRepository historyRepository,
                           TransactionService transactionService) {
        this.lostItemRepository = lostItemRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.transactionService = transactionService;
    }

    @Transactional
    public LostItemDto reportLostItem(LostItemDto dto, Long reporterUserId) {
        User reporter = userRepository.findById(reporterUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + reporterUserId));

        String code = "LOST-" + (1000 + (long)(Math.random() * 9000)) + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        LostItem item = new LostItem();
        item.setLostItemIdCode(code);
        item.setItemName(dto.getItemName().trim());
        item.setCategory(dto.getCategory().trim());
        item.setDescription(dto.getDescription());
        item.setDateLost(dto.getDateLost());
        item.setTimeLost(dto.getTimeLost());
        item.setLocationLost(dto.getLocationLost().trim());
        item.setColor(dto.getColor());
        item.setBrand(dto.getBrand());
        item.setCharacteristics(dto.getCharacteristics());
        item.setContactInfo(dto.getContactInfo() != null ? dto.getContactInfo() : reporter.getPhoneNumber());
        item.setImageUrl(dto.getImageUrl());
        item.setStatus(ItemStatus.REPORTED);
        item.setReporter(reporter);

        LostItem saved = lostItemRepository.save(item);

        recordStatusHistory(saved, null, ItemStatus.REPORTED, reporter, "Initial lost item report submitted");

        transactionService.recordTransaction("LOST", saved.getId(), saved.getLostItemIdCode(), reporter,
                "REPORT_LOST_ITEM", null, ItemStatus.REPORTED.name(),
                reporter.getFullName(), "N/A", "N/A", "Item reported lost: " + saved.getItemName());

        return new LostItemDto(saved);
    }

    public List<LostItemDto> getAllLostItems() {
        return lostItemRepository.findAll().stream()
                .map(LostItemDto::new)
                .collect(Collectors.toList());
    }

    public LostItemDto getLostItemById(Long id) {
        LostItem item = lostItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lost item not found with id: " + id));
        return new LostItemDto(item);
    }

    public LostItemDto getLostItemByCode(String code) {
        LostItem item = lostItemRepository.findByLostItemIdCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Lost item not found with code: " + code));
        return new LostItemDto(item);
    }

    public List<LostItemDto> getLostItemsByReporter(Long reporterUserId) {
        User reporter = userRepository.findById(reporterUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + reporterUserId));
        return lostItemRepository.findByReporter(reporter).stream()
                .map(LostItemDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public LostItemDto updateStatus(Long id, ItemStatus newStatus, User updatedBy, String comments) {
        LostItem item = lostItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lost item not found with id: " + id));

        ItemStatus oldStatus = item.getStatus();
        item.setStatus(newStatus);
        LostItem updated = lostItemRepository.save(item);

        recordStatusHistory(updated, oldStatus, newStatus, updatedBy, comments);

        transactionService.recordTransaction("LOST", updated.getId(), updated.getLostItemIdCode(), updatedBy,
                "UPDATE_STATUS", oldStatus.name(), newStatus.name(),
                updatedBy != null ? updatedBy.getFullName() : "Admin", "N/A", "N/A", comments);

        return new LostItemDto(updated);
    }

    private void recordStatusHistory(LostItem item, ItemStatus oldStatus, ItemStatus newStatus, User user, String comments) {
        ItemStatusHistory history = new ItemStatusHistory();
        history.setItemType("LOST");
        history.setItemId(item.getId());
        history.setItemCode(item.getLostItemIdCode());
        history.setPreviousStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedBy(user);
        history.setComments(comments);
        historyRepository.save(history);
    }
}
