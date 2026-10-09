package com.example.lostfound.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "qr_codes")
public class QrCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String qrCodeId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "found_item_id", nullable = false, unique = true)
    private FoundItem foundItem;

    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String qrCodeImageBase64;

    @Column(columnDefinition = "TEXT")
    private String verificationPayload;

    private boolean isUsed = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    public QrCode() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQrCodeId() {
        return qrCodeId;
    }

    public void setQrCodeId(String qrCodeId) {
        this.qrCodeId = qrCodeId;
    }

    public FoundItem getFoundItem() {
        return foundItem;
    }

    public void setFoundItem(FoundItem foundItem) {
        this.foundItem = foundItem;
    }

    public String getQrCodeImageBase64() {
        return qrCodeImageBase64;
    }

    public void setQrCodeImageBase64(String qrCodeImageBase64) {
        this.qrCodeImageBase64 = qrCodeImageBase64;
    }

    public String getVerificationPayload() {
        return verificationPayload;
    }

    public void setVerificationPayload(String verificationPayload) {
        this.verificationPayload = verificationPayload;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
