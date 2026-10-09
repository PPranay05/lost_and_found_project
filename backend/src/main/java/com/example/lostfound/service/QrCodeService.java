package com.example.lostfound.service;

import com.example.lostfound.dto.QrVerifyRequest;
import com.example.lostfound.dto.QrVerifyResponse;
import com.example.lostfound.entity.Claim;
import com.example.lostfound.entity.ClaimStatus;
import com.example.lostfound.entity.FoundItem;
import com.example.lostfound.entity.QrCode;
import com.example.lostfound.exception.InvalidQRCodeException;
import com.example.lostfound.exception.ResourceNotFoundException;
import com.example.lostfound.repository.ClaimRepository;
import com.example.lostfound.repository.FoundItemRepository;
import com.example.lostfound.repository.QrCodeRepository;
import com.example.lostfound.util.QrGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class QrCodeService {

    private final QrCodeRepository qrCodeRepository;
    private final FoundItemRepository foundItemRepository;
    private final ClaimRepository claimRepository;

    @Autowired
    public QrCodeService(QrCodeRepository qrCodeRepository,
                         FoundItemRepository foundItemRepository,
                         ClaimRepository claimRepository) {
        this.qrCodeRepository = qrCodeRepository;
        this.foundItemRepository = foundItemRepository;
        this.claimRepository = claimRepository;
    }

    @Transactional
    public QrCode generateQrCodeForFoundItem(FoundItem foundItem) {
        String qrCodeId = "QR-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        String payload = String.format("FOUND_ITEM_ID:%s|CODE:%s|NAME:%s|CATEGORY:%s|LOCATION:%s|DATE:%s|QR_ID:%s",
                foundItem.getId(),
                foundItem.getFoundItemIdCode(),
                foundItem.getItemName(),
                foundItem.getCategory(),
                foundItem.getLocationFound(),
                foundItem.getDateFound(),
                qrCodeId);

        String base64QrImage = QrGeneratorUtil.generateQrCodeBase64(payload);

        QrCode qrCode = new QrCode();
        qrCode.setQrCodeId(qrCodeId);
        qrCode.setFoundItem(foundItem);
        qrCode.setQrCodeImageBase64(base64QrImage);
        qrCode.setVerificationPayload(payload);
        qrCode.setUsed(false);

        return qrCodeRepository.save(qrCode);
    }

    public QrCode getQrCodeByFoundItemId(Long foundItemId) {
        return qrCodeRepository.findByFoundItemId(foundItemId)
                .orElseThrow(() -> new ResourceNotFoundException("QR Code not found for found item id: " + foundItemId));
    }

    public QrCode getQrCodeById(String qrCodeId) {
        return qrCodeRepository.findByQrCodeId(qrCodeId)
                .orElseThrow(() -> new ResourceNotFoundException("QR Code not found with ID: " + qrCodeId));
    }

    public byte[] getQrCodeImageBytes(Long foundItemId) {
        QrCode qrCode = getQrCodeByFoundItemId(foundItemId);
        return QrGeneratorUtil.generateQrCodeBytes(qrCode.getVerificationPayload());
    }

    public QrVerifyResponse verifyQrCode(QrVerifyRequest request) {
        String inputData = request.getQrCodeData().trim();
        QrCode qrCode = null;

        if (inputData.contains("data:image/")) {
            inputData = QrGeneratorUtil.decodeQrCodeFromBase64(inputData);
        }

        // 1. Check if input contains QR_ID payload
        if (inputData.contains("QR_ID:")) {
            String[] parts = inputData.split("\\|");
            for (String part : parts) {
                if (part.startsWith("QR_ID:")) {
                    String extractedQrId = part.substring("QR_ID:".length());
                    qrCode = qrCodeRepository.findByQrCodeId(extractedQrId).orElse(null);
                    break;
                }
            }
        }

        // 2. Direct QR ID lookup
        if (qrCode == null) {
            qrCode = qrCodeRepository.findByQrCodeId(inputData).orElse(null);
        }

        // 3. Lookup by Found Item Code (e.g. FOUND-2001-XXXX)
        if (qrCode == null) {
            FoundItem itemByCode = foundItemRepository.findByFoundItemIdCode(inputData).orElse(null);
            if (itemByCode != null) {
                qrCode = qrCodeRepository.findByFoundItem(itemByCode).orElse(null);
            }
        }

        // 4. Lookup by pipe-delimited CODE or FOUND_ITEM_ID
        if (qrCode == null && inputData.contains("|")) {
            String[] parts = inputData.split("\\|");
            for (String part : parts) {
                if (part.startsWith("CODE:")) {
                    String itemCode = part.substring("CODE:".length());
                    FoundItem item = foundItemRepository.findByFoundItemIdCode(itemCode).orElse(null);
                    if (item != null) {
                        qrCode = qrCodeRepository.findByFoundItem(item).orElse(null);
                        break;
                    }
                } else if (part.startsWith("FOUND_ITEM_ID:")) {
                    try {
                        Long foundItemId = Long.parseLong(part.substring("FOUND_ITEM_ID:".length()));
                        qrCode = qrCodeRepository.findByFoundItemId(foundItemId).orElse(null);
                        if (qrCode != null) break;
                    } catch (Exception ignored) {}
                }
            }
        }

        // 5. Lookup by numeric Found Item ID
        if (qrCode == null) {
            try {
                Long foundItemId = Long.parseLong(inputData);
                qrCode = qrCodeRepository.findByFoundItemId(foundItemId).orElse(null);
            } catch (NumberFormatException ignored) {}
        }

        QrVerifyResponse response = new QrVerifyResponse();
        response.setVerificationTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        if (qrCode == null) {
            response.setValid(false);
            response.setMessage("Invalid QR Code: No matching item record found for input '" + inputData + "'");
            return response;
        }

        FoundItem item = qrCode.getFoundItem();
        response.setValid(true);
        response.setQrCodeId(qrCode.getQrCodeId());
        response.setFoundItem(new com.example.lostfound.dto.FoundItemDto(item));
        response.setMessage("QR Code Verified Successfully. Found Item: " + item.getItemName() + " (" + item.getFoundItemIdCode() + ")");

        // Claim correlation logic
        Claim targetClaim = null;

        if (request.getClaimId() != null) {
            targetClaim = claimRepository.findById(request.getClaimId()).orElse(null);
        }

        // If no claim ID specified, auto-discover active claims for this found item
        if (targetClaim == null) {
            List<Claim> itemClaims = claimRepository.findByFoundItem(item);
            // Prioritize PENDING or UNDER_REVIEW claims
            targetClaim = itemClaims.stream()
                    .filter(c -> c.getStatus() == ClaimStatus.PENDING || c.getStatus() == ClaimStatus.UNDER_REVIEW)
                    .findFirst()
                    .orElse(itemClaims.isEmpty() ? null : itemClaims.get(itemClaims.size() - 1));
        }

        if (targetClaim != null) {
            response.setClaim(new com.example.lostfound.dto.ClaimRequestDto(targetClaim));
            if (targetClaim.getClaimant() != null) {
                response.setClaimant(new com.example.lostfound.dto.UserDto(targetClaim.getClaimant()));
            }

            if (!targetClaim.getFoundItem().getId().equals(item.getId())) {
                response.setValid(false);
                response.setMessage("QR Verification Failed: QR Code belongs to item " + item.getFoundItemIdCode() + " but claim is for item " + targetClaim.getFoundItem().getFoundItemIdCode());
            }
        }

        return response;
    }
}
