package com.example.lostfound.dto;

import com.example.lostfound.entity.Transaction;

import java.time.LocalDateTime;

public class TransactionDto {

    private Long id;
    private String transactionCode;
    private String itemType;
    private Long itemId;
    private String itemCode;
    private Long userId;
    private String userName;
    private String userEmail;
    private String action;
    private String previousStatus;
    private String newStatus;
    private LocalDateTime timestamp;
    private String adminOrUserResponsible;
    private String qrVerificationResult;
    private String claimResult;
    private String notes;

    public TransactionDto() {}

    public TransactionDto(Transaction t) {
        this.id = t.getId();
        this.transactionCode = t.getTransactionCode();
        this.itemType = t.getItemType();
        this.itemId = t.getItemId();
        this.itemCode = t.getItemCode();
        if (t.getUser() != null) {
            this.userId = t.getUser().getId();
            this.userName = t.getUser().getFullName();
            this.userEmail = t.getUser().getEmail();
        }
        this.action = t.getAction();
        this.previousStatus = t.getPreviousStatus();
        this.newStatus = t.getNewStatus();
        this.timestamp = t.getTimestamp();
        this.adminOrUserResponsible = t.getAdminOrUserResponsible();
        this.qrVerificationResult = t.getQrVerificationResult();
        this.claimResult = t.getClaimResult();
        this.notes = t.getNotes();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getAdminOrUserResponsible() {
        return adminOrUserResponsible;
    }

    public void setAdminOrUserResponsible(String adminOrUserResponsible) {
        this.adminOrUserResponsible = adminOrUserResponsible;
    }

    public String getQrVerificationResult() {
        return qrVerificationResult;
    }

    public void setQrVerificationResult(String qrVerificationResult) {
        this.qrVerificationResult = qrVerificationResult;
    }

    public String getClaimResult() {
        return claimResult;
    }

    public void setClaimResult(String claimResult) {
        this.claimResult = claimResult;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
