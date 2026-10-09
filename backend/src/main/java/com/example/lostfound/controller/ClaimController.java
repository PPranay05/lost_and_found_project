package com.example.lostfound.controller;

import com.example.lostfound.dto.ClaimRequestDto;
import com.example.lostfound.dto.UserDto;
import com.example.lostfound.entity.ClaimStatus;
import com.example.lostfound.entity.User;
import com.example.lostfound.service.ClaimService;
import com.example.lostfound.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
@Tag(name = "Claims", description = "Submit and process item ownership claim requests")
public class ClaimController {

    private final ClaimService claimService;
    private final UserService userService;

    @Autowired
    public ClaimController(ClaimService claimService, UserService userService) {
        this.claimService = claimService;
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Submit an ownership claim request for a found item")
    public ResponseEntity<ClaimRequestDto> submitClaim(@Valid @RequestBody ClaimRequestDto dto, HttpSession session) {
        UserDto loggedIn = (UserDto) session.getAttribute("LOGGED_IN_USER");
        if (loggedIn == null) {
            throw new com.example.lostfound.exception.UnauthorizedException("You must be logged in to submit an ownership claim. Please login or register.");
        }
        ClaimRequestDto created = claimService.submitClaim(dto, loggedIn.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all ownership claim requests (Admin feature)")
    public ResponseEntity<List<ClaimRequestDto>> getAllClaims() {
        return ResponseEntity.ok(claimService.getAllClaims());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get claim details by ID")
    public ResponseEntity<ClaimRequestDto> getClaimById(@PathVariable Long id) {
        return ResponseEntity.ok(claimService.getClaimById(id));
    }

    @GetMapping("/my")
    @Operation(summary = "Get claims submitted by current logged-in user")
    public ResponseEntity<List<ClaimRequestDto>> getMyClaims(HttpSession session) {
        UserDto loggedIn = (UserDto) session.getAttribute("LOGGED_IN_USER");
        if (loggedIn == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(claimService.getClaimsByClaimant(loggedIn.getId()));
    }

    @PutMapping("/{id}/review")
    @Operation(summary = "Admin review and approve or reject a claim request")
    public ResponseEntity<ClaimRequestDto> reviewClaim(@PathVariable Long id,
                                                        @RequestParam ClaimStatus status,
                                                        @RequestParam(required = false) String adminNotes,
                                                        @RequestParam(required = false) String qrVerificationResult,
                                                        HttpSession session) {
        UserDto loggedIn = (UserDto) session.getAttribute("LOGGED_IN_USER");
        User adminUser = loggedIn != null ? userService.getUserEntityById(loggedIn.getId()) : null;
        ClaimRequestDto updated = claimService.reviewClaim(id, status, adminNotes, qrVerificationResult, adminUser);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/return")
    @Operation(summary = "Admin mark item as physically returned to owner")
    public ResponseEntity<ClaimRequestDto> markItemReturned(@PathVariable Long id,
                                                           @RequestParam(required = false, defaultValue = "Item handed over to verified owner") String notes,
                                                           HttpSession session) {
        UserDto loggedIn = (UserDto) session.getAttribute("LOGGED_IN_USER");
        User adminUser = loggedIn != null ? userService.getUserEntityById(loggedIn.getId()) : null;
        ClaimRequestDto updated = claimService.markItemReturned(id, adminUser, notes);
        return ResponseEntity.ok(updated);
    }
}
