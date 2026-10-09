package com.example.lostfound.dto;

import com.example.lostfound.entity.FoundItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class FoundItemDto {

    private Long id;
    private String foundItemIdCode;

    @NotBlank(message = "Item name is required")
    private String itemName;

    @NotBlank(message = "Category is required")
    private String category;

    private String description;

    @NotNull(message = "Date found is required")
    private LocalDate dateFound;

    private LocalTime timeFound;

    @NotBlank(message = "Location found is required")
    private String locationFound;

    private String color;
    private String brand;
    private String characteristics;
    private String imageUrl;
    private String status;
    private Long finderId;
    private String finderName;
    private String finderEmail;
    private String qrCodeId;
    private String qrCodeImageBase64;
    private LocalDateTime createdAt;

    public FoundItemDto() {}

    public FoundItemDto(FoundItem item) {
        this.id = item.getId();
        this.foundItemIdCode = item.getFoundItemIdCode();
        this.itemName = item.getItemName();
        this.category = item.getCategory();
        this.description = item.getDescription();
        this.dateFound = item.getDateFound();
        this.timeFound = item.getTimeFound();
        this.locationFound = item.getLocationFound();
        this.color = item.getColor();
        this.brand = item.getBrand();
        this.characteristics = item.getCharacteristics();
        this.imageUrl = item.getImageUrl();
        this.status = item.getStatus().name();
        if (item.getFinder() != null) {
            this.finderId = item.getFinder().getId();
            this.finderName = item.getFinder().getFullName();
            this.finderEmail = item.getFinder().getEmail();
        }
        this.createdAt = item.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFoundItemIdCode() {
        return foundItemIdCode;
    }

    public void setFoundItemIdCode(String foundItemIdCode) {
        this.foundItemIdCode = foundItemIdCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateFound() {
        return dateFound;
    }

    public void setDateFound(LocalDate dateFound) {
        this.dateFound = dateFound;
    }

    public LocalTime getTimeFound() {
        return timeFound;
    }

    public void setTimeFound(LocalTime timeFound) {
        this.timeFound = timeFound;
    }

    public String getLocationFound() {
        return locationFound;
    }

    public void setLocationFound(String locationFound) {
        this.locationFound = locationFound;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(String characteristics) {
        this.characteristics = characteristics;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getFinderId() {
        return finderId;
    }

    public void setFinderId(Long finderId) {
        this.finderId = finderId;
    }

    public String getFinderName() {
        return finderName;
    }

    public void setFinderName(String finderName) {
        this.finderName = finderName;
    }

    public String getFinderEmail() {
        return finderEmail;
    }

    public void setFinderEmail(String finderEmail) {
        this.finderEmail = finderEmail;
    }

    public String getQrCodeId() {
        return qrCodeId;
    }

    public void setQrCodeId(String qrCodeId) {
        this.qrCodeId = qrCodeId;
    }

    public String getQrCodeImageBase64() {
        return qrCodeImageBase64;
    }

    public void setQrCodeImageBase64(String qrCodeImageBase64) {
        this.qrCodeImageBase64 = qrCodeImageBase64;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
