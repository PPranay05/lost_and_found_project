package com.example.lostfound.service;

import com.example.lostfound.dto.FoundItemDto;
import com.example.lostfound.entity.FoundItem;
import com.example.lostfound.entity.ItemStatus;
import com.example.lostfound.entity.ItemStatusHistory;
import com.example.lostfound.entity.QrCode;
import com.example.lostfound.entity.User;
import com.example.lostfound.exception.ResourceNotFoundException;
import com.example.lostfound.repository.FoundItemRepository;
import com.example.lostfound.repository.ItemStatusHistoryRepository;
import com.example.lostfound.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FoundItemService {

    private final FoundItemRepository foundItemRepository;
    private final UserRepository userRepository;
    private final QrCodeService qrCodeService;
    private final ItemStatusHistoryRepository historyRepository;
    private final TransactionService transactionService;

    @Autowired
    public FoundItemService(FoundItemRepository foundItemRepository,
                             UserRepository userRepository,
                             QrCodeService qrCodeService,
                             ItemStatusHistoryRepository historyRepository,
                             TransactionService transactionService) {
        this.foundItemRepository = foundItemRepository;
        this.userRepository = userRepository;
        this.qrCodeService = qrCodeService;
        this.historyRepository = historyRepository;
        this.transactionService = transactionService;
    }

    @Transactional
    public FoundItemDto registerFoundItem(FoundItemDto dto, Long finderUserId) {
        User finder = userRepository.findById(finderUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + finderUserId));

        String code = "FOUND-" + (2000 + (long)(Math.random() * 8000)) + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        FoundItem item = new FoundItem();
        item.setFoundItemIdCode(code);
        item.setItemName(dto.getItemName().trim());
        item.setCategory(dto.getCategory().trim());
        item.setDescription(dto.getDescription());
        item.setDateFound(dto.getDateFound());
        item.setTimeFound(dto.getTimeFound());
        item.setLocationFound(dto.getLocationFound().trim());
        item.setColor(dto.getColor());
        item.setBrand(dto.getBrand());
        item.setCharacteristics(dto.getCharacteristics());
        item.setImageUrl(dto.getImageUrl());
        item.setStatus(ItemStatus.REPORTED);
        item.setFinder(finder);

        FoundItem saved = foundItemRepository.save(item);

        // AUTOMATICALLY GENERATE QR CODE FOR EVERY REGISTERED FOUND ITEM
        QrCode qrCode = qrCodeService.generateQrCodeForFoundItem(saved);

        recordStatusHistory(saved, null, ItemStatus.REPORTED, finder, "Found item registered with automatic QR code generation");

        transactionService.recordTransaction("FOUND", saved.getId(), saved.getFoundItemIdCode(), finder,
                "REGISTER_FOUND_ITEM", null, ItemStatus.REPORTED.name(),
                finder.getFullName(), "QR Code Generated: " + qrCode.getQrCodeId(), "N/A", "Item registered found: " + saved.getItemName());

        FoundItemDto result = new FoundItemDto(saved);
        result.setQrCodeId(qrCode.getQrCodeId());
        result.setQrCodeImageBase64(qrCode.getQrCodeImageBase64());
        return result;
    }

    public List<FoundItemDto> getAllFoundItems() {
        return foundItemRepository.findAll().stream()
                .map(item -> {
                    FoundItemDto dto = new FoundItemDto(item);
                    try {
                        QrCode qr = qrCodeService.getQrCodeByFoundItemId(item.getId());
                        dto.setQrCodeId(qr.getQrCodeId());
                        dto.setQrCodeImageBase64(qr.getQrCodeImageBase64());
                    } catch (Exception ignored) {}
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public FoundItemDto getFoundItemById(Long id) {
        FoundItem item = foundItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Found item not found with id: " + id));
        FoundItemDto dto = new FoundItemDto(item);
        try {
            QrCode qr = qrCodeService.getQrCodeByFoundItemId(item.getId());
            dto.setQrCodeId(qr.getQrCodeId());
            dto.setQrCodeImageBase64(qr.getQrCodeImageBase64());
        } catch (Exception ignored) {}
        return dto;
    }

    public FoundItemDto getFoundItemByCode(String code) {
        FoundItem item = foundItemRepository.findByFoundItemIdCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Found item not found with code: " + code));
        FoundItemDto dto = new FoundItemDto(item);
        try {
            QrCode qr = qrCodeService.getQrCodeByFoundItemId(item.getId());
            dto.setQrCodeId(qr.getQrCodeId());
            dto.setQrCodeImageBase64(qr.getQrCodeImageBase64());
        } catch (Exception ignored) {}
        return dto;
    }

    public List<FoundItemDto> getFoundItemsByFinder(Long finderUserId) {
        User finder = userRepository.findById(finderUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + finderUserId));
        return foundItemRepository.findByFinder(finder).stream()
                .map(item -> {
                    FoundItemDto dto = new FoundItemDto(item);
                    try {
                        QrCode qr = qrCodeService.getQrCodeByFoundItemId(item.getId());
                        dto.setQrCodeId(qr.getQrCodeId());
                        dto.setQrCodeImageBase64(qr.getQrCodeImageBase64());
                    } catch (Exception ignored) {}
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public FoundItemDto updateStatus(Long id, ItemStatus newStatus, User updatedBy, String comments) {
        FoundItem item = foundItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Found item not found with id: " + id));

        ItemStatus oldStatus = item.getStatus();
        item.setStatus(newStatus);
        FoundItem updated = foundItemRepository.save(item);

        recordStatusHistory(updated, oldStatus, newStatus, updatedBy, comments);

        transactionService.recordTransaction("FOUND", updated.getId(), updated.getFoundItemIdCode(), updatedBy,
                "UPDATE_STATUS", oldStatus.name(), newStatus.name(),
                updatedBy != null ? updatedBy.getFullName() : "Admin", "N/A", "N/A", comments);

        FoundItemDto dto = new FoundItemDto(updated);
        try {
            QrCode qr = qrCodeService.getQrCodeByFoundItemId(updated.getId());
            dto.setQrCodeId(qr.getQrCodeId());
            dto.setQrCodeImageBase64(qr.getQrCodeImageBase64());
        } catch (Exception ignored) {}
        return dto;
    }

    private void recordStatusHistory(FoundItem item, ItemStatus oldStatus, ItemStatus newStatus, User user, String comments) {
        ItemStatusHistory history = new ItemStatusHistory();
        history.setItemType("FOUND");
        history.setItemId(item.getId());
        history.setItemCode(item.getFoundItemIdCode());
        history.setPreviousStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedBy(user);
        history.setComments(comments);
        historyRepository.save(history);
    }
}
