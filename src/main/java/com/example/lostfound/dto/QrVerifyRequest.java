package com.example.lostfound.dto;

import jakarta.validation.constraints.NotBlank;

public class QrVerifyRequest {

    @NotBlank(message = "QR Code content or ID is required")
    private String qrCodeData;

    private Long claimId;
    private String adminNotes;

    public QrVerifyRequest() {}

    public QrVerifyRequest(String qrCodeData, Long claimId) {
        this.qrCodeData = qrCodeData;
        this.claimId = claimId;
    }

    public String getQrCodeData() {
        return qrCodeData;
    }

    public void setQrCodeData(String qrCodeData) {
        this.qrCodeData = qrCodeData;
    }

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }
}
