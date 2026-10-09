package com.example.lostfound.dto;

import com.example.lostfound.entity.Claim;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ClaimRequestDto {

    private Long id;
    private String claimIdCode;

    @NotNull(message = "Found item ID is required")
    private Long foundItemId;

    private Long lostItemId;

    private Long claimantId;

    @NotBlank(message = "Claimant name is required")
    private String claimantName;

    @NotBlank(message = "Contact details are required")
    private String contactDetails;

    @NotBlank(message = "Item description is required")
    private String itemDescription;

    private String identifyingDetails;
    private String proofDetails;
    private String status;
    private String adminNotes;
    private String qrVerificationResult;

    private String foundItemName;
    private String foundItemCode;
    private String foundItemLocation;
    private String claimantEmail;
    private String claimantStudentEmpId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ClaimRequestDto() {}

    public ClaimRequestDto(Claim claim) {
        this.id = claim.getId();
        this.claimIdCode = claim.getClaimIdCode();
        if (claim.getFoundItem() != null) {
            this.foundItemId = claim.getFoundItem().getId();
            this.foundItemName = claim.getFoundItem().getItemName();
            this.foundItemCode = claim.getFoundItem().getFoundItemIdCode();
            this.foundItemLocation = claim.getFoundItem().getLocationFound();
        }
        if (claim.getLostItem() != null) {
            this.lostItemId = claim.getLostItem().getId();
        }
        if (claim.getClaimant() != null) {
            this.claimantId = claim.getClaimant().getId();
            this.claimantEmail = claim.getClaimant().getEmail();
            this.claimantStudentEmpId = claim.getClaimant().getStudentEmpId();
        }
        this.claimantName = claim.getClaimantName();
        this.contactDetails = claim.getContactDetails();
        this.itemDescription = claim.getItemDescription();
        this.identifyingDetails = claim.getIdentifyingDetails();
        this.proofDetails = claim.getProofDetails();
        this.status = claim.getStatus().name();
        this.adminNotes = claim.getAdminNotes();
        this.qrVerificationResult = claim.getQrVerificationResult();
        this.createdAt = claim.getCreatedAt();
        this.updatedAt = claim.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClaimIdCode() {
        return claimIdCode;
    }

    public void setClaimIdCode(String claimIdCode) {
        this.claimIdCode = claimIdCode;
    }

    public Long getFoundItemId() {
        return foundItemId;
    }

    public void setFoundItemId(Long foundItemId) {
        this.foundItemId = foundItemId;
    }

    public Long getLostItemId() {
        return lostItemId;
    }

    public void setLostItemId(Long lostItemId) {
        this.lostItemId = lostItemId;
    }

    public Long getClaimantId() {
        return claimantId;
    }

    public void setClaimantId(Long claimantId) {
        this.claimantId = claimantId;
    }

    public String getClaimantName() {
        return claimantName;
    }

    public void setClaimantName(String claimantName) {
        this.claimantName = claimantName;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getIdentifyingDetails() {
        return identifyingDetails;
    }

    public void setIdentifyingDetails(String identifyingDetails) {
        this.identifyingDetails = identifyingDetails;
    }

    public String getProofDetails() {
        return proofDetails;
    }

    public void setProofDetails(String proofDetails) {
        this.proofDetails = proofDetails;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public String getQrVerificationResult() {
        return qrVerificationResult;
    }

    public void setQrVerificationResult(String qrVerificationResult) {
        this.qrVerificationResult = qrVerificationResult;
    }

    public String getFoundItemName() {
        return foundItemName;
    }

    public void setFoundItemName(String foundItemName) {
        this.foundItemName = foundItemName;
    }

    public String getFoundItemCode() {
        return foundItemCode;
    }

    public void setFoundItemCode(String foundItemCode) {
        this.foundItemCode = foundItemCode;
    }

    public String getFoundItemLocation() {
        return foundItemLocation;
    }

    public void setFoundItemLocation(String foundItemLocation) {
        this.foundItemLocation = foundItemLocation;
    }

    public String getClaimantEmail() {
        return claimantEmail;
    }

    public void setClaimantEmail(String claimantEmail) {
        this.claimantEmail = claimantEmail;
    }

    public String getClaimantStudentEmpId() {
        return claimantStudentEmpId;
    }

    public void setClaimantStudentEmpId(String claimantStudentEmpId) {
        this.claimantStudentEmpId = claimantStudentEmpId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
