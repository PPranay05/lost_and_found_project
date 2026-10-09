package com.example.lostfound.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String transactionCode;

    private String itemType; // LOST or FOUND
    private Long itemId;
    private String itemCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    private String action;
    private String previousStatus;
    private String newStatus;
    private LocalDateTime timestamp = LocalDateTime.now();
    private String adminOrUserResponsible;
    private String qrVerificationResult;
    private String claimResult;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public Transaction() {}

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
