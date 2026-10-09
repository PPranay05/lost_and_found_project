package com.example.lostfound;

import com.example.lostfound.dto.*;
import com.example.lostfound.entity.ClaimStatus;
import com.example.lostfound.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LostFoundApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private LostItemService lostItemService;

    @Autowired
    private FoundItemService foundItemService;

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private ClaimService claimService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private TransactionService transactionService;

    @Test
    void contextLoads() {
        assertNotNull(userService);
        assertNotNull(lostItemService);
        assertNotNull(foundItemService);
        assertNotNull(qrCodeService);
        assertNotNull(claimService);
    }

    @Test
    void testCompleteLostFoundAndQrVerificationWorkflow() {
        // Step 1: Register New User
        RegisterRequest regReq = new RegisterRequest();
        regReq.setFullName("Test Student");
        regReq.setEmail("teststudent" + System.currentTimeMillis() + "@campus.edu");
        regReq.setPhoneNumber("+1 555-9999");
        regReq.setStudentEmpId("STU-9999");
        regReq.setPassword("password123");
        regReq.setConfirmPassword("password123");
        regReq.setRole("USER");

        AuthResponse regResp = userService.register(regReq);
        assertNotNull(regResp.getUser().getId());

        // Step 2: Login User
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail(regReq.getEmail());
        loginReq.setPassword("password123");
        AuthResponse loginResp = userService.login(loginReq);
        assertEquals(regReq.getEmail(), loginResp.getUser().getEmail());

        // Step 3: Report Lost Item
        LostItemDto lostReq = new LostItemDto();
        lostReq.setItemName("Blue Airpods Pro");
        lostReq.setCategory("Electronics");
        lostReq.setDescription("Blue wireless earbuds in protective case");
        lostReq.setDateLost(LocalDate.now());
        lostReq.setTimeLost(LocalTime.of(12, 0));
        lostReq.setLocationLost("Auditorium Hall B");
        lostReq.setColor("Blue");
        lostReq.setBrand("Apple");
        LostItemDto reportedLost = lostItemService.reportLostItem(lostReq, regResp.getUser().getId());
        assertEquals("REPORTED", reportedLost.getStatus());
        assertTrue(reportedLost.getLostItemIdCode().startsWith("LOST-"));

        // Step 4: Register Found Item (Automatic QR Code Generation)
        FoundItemDto foundReq = new FoundItemDto();
        foundReq.setItemName("Blue Airpods Earbuds Case");
        foundReq.setCategory("Electronics");
        foundReq.setDescription("Blue charging case found under seats");
        foundReq.setDateFound(LocalDate.now());
        foundReq.setTimeFound(LocalTime.of(12, 30));
        foundReq.setLocationFound("Auditorium Hall B");
        foundReq.setColor("Blue");
        foundReq.setBrand("Apple");
        FoundItemDto registeredFound = foundItemService.registerFoundItem(foundReq, 1L); // Admin finder ID
        assertEquals("REPORTED", registeredFound.getStatus());
        assertNotNull(registeredFound.getQrCodeId());
        assertTrue(registeredFound.getQrCodeImageBase64().startsWith("data:image/png;base64,"));

        // Step 5: Search Listings
        SearchRequest searchReq = new SearchRequest();
        searchReq.setQuery("Airpods");
        searchReq.setType("ALL");
        Map<String, Object> searchResults = searchService.searchItems(searchReq);
        assertTrue(((List<?>) searchResults.get("lostItems")).size() >= 1);
        assertTrue(((List<?>) searchResults.get("foundItems")).size() >= 1);

        // Step 6: Submit Ownership Claim
        ClaimRequestDto claimReq = new ClaimRequestDto();
        claimReq.setFoundItemId(registeredFound.getId());
        claimReq.setLostItemId(reportedLost.getId());
        claimReq.setClaimantName(regReq.getFullName());
        claimReq.setContactDetails(regReq.getPhoneNumber());
        claimReq.setItemDescription("Blue Airpods case with Apple serial number");
        claimReq.setIdentifyingDetails("Engraved initials TS on bottom");

        ClaimRequestDto submittedClaim = claimService.submitClaim(claimReq, regResp.getUser().getId());
        assertEquals("PENDING", submittedClaim.getStatus());

        // Step 7: Scan & Verify QR Code
        QrVerifyRequest qrReq = new QrVerifyRequest(registeredFound.getQrCodeId(), submittedClaim.getId());
        QrVerifyResponse qrResp = qrCodeService.verifyQrCode(qrReq);
        assertTrue(qrResp.isValid());
        assertEquals(registeredFound.getId(), qrResp.getFoundItem().getId());

        // Step 8: Admin Review & Approve Claim
        ClaimRequestDto approvedClaim = claimService.reviewClaim(
                submittedClaim.getId(),
                ClaimStatus.APPROVED,
                "Ownership verified via QR Code scan and serial number match",
                "QR Verified: Valid code " + registeredFound.getQrCodeId(),
                null
        );
        assertEquals("APPROVED", approvedClaim.getStatus());

        // Verify Status transition to CLAIMED
        FoundItemDto updatedFound = foundItemService.getFoundItemById(registeredFound.getId());
        assertEquals("CLAIMED", updatedFound.getStatus());

        // Step 9: Mark Item physically Returned
        ClaimRequestDto returnedClaim = claimService.markItemReturned(submittedClaim.getId(), null, "Handed over at Admin Office");
        FoundItemDto finalFound = foundItemService.getFoundItemById(registeredFound.getId());
        assertEquals("RETURNED", finalFound.getStatus());

        // Step 10: Verify Audit Transaction Logged
        List<TransactionDto> txns = transactionService.getAllTransactions();
        assertFalse(txns.isEmpty());
    }
}
