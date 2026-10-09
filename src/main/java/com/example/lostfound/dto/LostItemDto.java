package com.example.lostfound.dto;

import com.example.lostfound.entity.LostItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class LostItemDto {

    private Long id;
    private String lostItemIdCode;

    @NotBlank(message = "Item name is required")
    private String itemName;

    @NotBlank(message = "Category is required")
    private String category;

    private String description;

    @NotNull(message = "Date lost is required")
    private LocalDate dateLost;

    private LocalTime timeLost;

    @NotBlank(message = "Location lost is required")
    private String locationLost;

    private String color;
    private String brand;
    private String characteristics;
    private String contactInfo;
    private String imageUrl;
    private String status;
    private Long reporterId;
    private String reporterName;
    private String reporterEmail;
    private LocalDateTime createdAt;

    public LostItemDto() {}

    public LostItemDto(LostItem item) {
        this.id = item.getId();
        this.lostItemIdCode = item.getLostItemIdCode();
        this.itemName = item.getItemName();
        this.category = item.getCategory();
        this.description = item.getDescription();
        this.dateLost = item.getDateLost();
        this.timeLost = item.getTimeLost();
        this.locationLost = item.getLocationLost();
        this.color = item.getColor();
        this.brand = item.getBrand();
        this.characteristics = item.getCharacteristics();
        this.contactInfo = item.getContactInfo();
        this.imageUrl = item.getImageUrl();
        this.status = item.getStatus().name();
        if (item.getReporter() != null) {
            this.reporterId = item.getReporter().getId();
            this.reporterName = item.getReporter().getFullName();
            this.reporterEmail = item.getReporter().getEmail();
        }
        this.createdAt = item.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLostItemIdCode() {
        return lostItemIdCode;
    }

    public void setLostItemIdCode(String lostItemIdCode) {
        this.lostItemIdCode = lostItemIdCode;
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

    public LocalDate getDateLost() {
        return dateLost;
    }

    public void setDateLost(LocalDate dateLost) {
        this.dateLost = dateLost;
    }

    public LocalTime getTimeLost() {
        return timeLost;
    }

    public void setTimeLost(LocalTime timeLost) {
        this.timeLost = timeLost;
    }

    public String getLocationLost() {
        return locationLost;
    }

    public void setLocationLost(String locationLost) {
        this.locationLost = locationLost;
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

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
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

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getReporterEmail() {
        return reporterEmail;
    }

    public void setReporterEmail(String reporterEmail) {
        this.reporterEmail = reporterEmail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
