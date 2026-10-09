package com.example.lostfound.controller;

import com.example.lostfound.dto.QrVerifyRequest;
import com.example.lostfound.dto.QrVerifyResponse;
import com.example.lostfound.entity.FoundItem;
import com.example.lostfound.entity.QrCode;
import com.example.lostfound.repository.FoundItemRepository;
import com.example.lostfound.service.QrCodeService;
import com.example.lostfound.util.QrGeneratorUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/qr")
@Tag(name = "QR Code Verification", description = "QR Code generation, scanning, and authentication verification")
public class QrController {

    private final QrCodeService qrCodeService;
    private final FoundItemRepository foundItemRepository;

    @Autowired
    public QrController(QrCodeService qrCodeService, FoundItemRepository foundItemRepository) {
        this.qrCodeService = qrCodeService;
        this.foundItemRepository = foundItemRepository;
    }

    @PostMapping("/generate/{foundItemId}")
    @Operation(summary = "Generate QR Code for a specific found item")
    public ResponseEntity<Map<String, String>> generateQrCode(@PathVariable Long foundItemId) {
        FoundItem foundItem = foundItemRepository.findById(foundItemId)
                .orElseThrow(() -> new com.example.lostfound.exception.ResourceNotFoundException("Found item not found with id: " + foundItemId));

        QrCode qrCode = qrCodeService.generateQrCodeForFoundItem(foundItem);

        Map<String, String> response = new HashMap<>();
        response.put("qrCodeId", qrCode.getQrCodeId());
        response.put("foundItemId", foundItem.getId().toString());
        response.put("foundItemCode", foundItem.getFoundItemIdCode());
        response.put("qrImageBase64", qrCode.getQrCodeImageBase64());
        response.put("verificationPayload", qrCode.getVerificationPayload());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/image/{foundItemId}")
    @Operation(summary = "Download or stream QR Code PNG image for attachment to physical item")
    public ResponseEntity<byte[]> getQrCodeImage(@PathVariable Long foundItemId) {
        byte[] imageBytes = qrCodeService.getQrCodeImageBytes(foundItemId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentDispositionFormData("attachment", "qrcode-found-item-" + foundItemId + ".png");
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify scanned QR Code against database and cross-check claimant details")
    public ResponseEntity<QrVerifyResponse> verifyQrCode(@Valid @RequestBody QrVerifyRequest request) {
        QrVerifyResponse response = qrCodeService.verifyQrCode(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/decode")
    @Operation(summary = "Upload image file to decode embedded QR code")
    public ResponseEntity<Map<String, String>> decodeQrImage(@RequestParam("file") MultipartFile file) {
        Map<String, String> result = new HashMap<>();
        try {
            String base64 = Base64.getEncoder().encodeToString(file.getBytes());
            String decodedText = QrGeneratorUtil.decodeQrCodeFromBase64("data:image/png;base64," + base64);
            result.put("status", "SUCCESS");
            result.put("decodedText", decodedText);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", e.getMessage() != null ? e.getMessage() : "Failed to read QR code from uploaded image");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }
}
