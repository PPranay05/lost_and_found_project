package com.example.lostfound.controller;

import com.example.lostfound.dto.LostItemDto;
import com.example.lostfound.dto.UserDto;
import com.example.lostfound.service.LostItemService;
import com.example.lostfound.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lost-items")
@Tag(name = "Lost Items", description = "Report and manage lost item listings")
public class LostItemController {

    private final LostItemService lostItemService;
    private final AuthUtil authUtil;

    @Autowired
    public LostItemController(LostItemService lostItemService, AuthUtil authUtil) {
        this.lostItemService = lostItemService;
        this.authUtil = authUtil;
    }

    @PostMapping
    @Operation(summary = "Submit a report for a lost item")
    public ResponseEntity<LostItemDto> reportLostItem(@Valid @RequestBody LostItemDto dto, HttpSession session, HttpServletRequest request) {
        UserDto loggedIn = authUtil.getAuthenticatedUser(session, request);
        if (loggedIn == null) {
            throw new com.example.lostfound.exception.UnauthorizedException("You must be logged in to report a lost item. Please login or register.");
        }
        LostItemDto created = lostItemService.reportLostItem(dto, loggedIn.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all reported lost items")
    public ResponseEntity<List<LostItemDto>> getAllLostItems() {
        return ResponseEntity.ok(lostItemService.getAllLostItems());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lost item details by ID")
    public ResponseEntity<LostItemDto> getLostItemById(@PathVariable Long id) {
        return ResponseEntity.ok(lostItemService.getLostItemById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get lost item details by item code")
    public ResponseEntity<LostItemDto> getLostItemByCode(@PathVariable String code) {
        return ResponseEntity.ok(lostItemService.getLostItemByCode(code));
    }

    @GetMapping("/my")
    @Operation(summary = "Get lost item reports submitted by current logged-in user")
    public ResponseEntity<List<LostItemDto>> getMyLostItems(HttpSession session, HttpServletRequest request) {
        UserDto loggedIn = authUtil.getAuthenticatedUser(session, request);
        if (loggedIn == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(lostItemService.getLostItemsByReporter(loggedIn.getId()));
    }
}
