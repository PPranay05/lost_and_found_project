package com.example.lostfound.dto;

public class QrVerifyResponse {

    private boolean valid;
    private String message;
    private String qrCodeId;
    private FoundItemDto foundItem;
    private ClaimRequestDto claim;
    private UserDto claimant;
    private String verificationTimestamp;

    public QrVerifyResponse() {}

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getQrCodeId() {
        return qrCodeId;
    }

    public void setQrCodeId(String qrCodeId) {
        this.qrCodeId = qrCodeId;
    }

    public FoundItemDto getFoundItem() {
        return foundItem;
    }

    public void setFoundItem(FoundItemDto foundItem) {
        this.foundItem = foundItem;
    }

    public ClaimRequestDto getClaim() {
        return claim;
    }

    public void setClaim(ClaimRequestDto claim) {
        this.claim = claim;
    }

    public UserDto getClaimant() {
        return claimant;
    }

    public void setClaimant(UserDto claimant) {
        this.claimant = claimant;
    }

    public String getVerificationTimestamp() {
        return verificationTimestamp;
    }

    public void setVerificationTimestamp(String verificationTimestamp) {
        this.verificationTimestamp = verificationTimestamp;
    }
}
