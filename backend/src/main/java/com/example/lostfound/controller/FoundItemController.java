package com.example.lostfound.controller;

import com.example.lostfound.dto.FoundItemDto;
import com.example.lostfound.dto.UserDto;
import com.example.lostfound.service.FoundItemService;
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
@RequestMapping("/api/found-items")
@Tag(name = "Found Items", description = "Register found items and automatically generate QR codes")
public class FoundItemController {

    private final FoundItemService foundItemService;
    private final AuthUtil authUtil;

    @Autowired
    public FoundItemController(FoundItemService foundItemService, AuthUtil authUtil) {
        this.foundItemService = foundItemService;
        this.authUtil = authUtil;
    }

    @PostMapping
    @Operation(summary = "Register a found item (automatically receives QR Code)")
    public ResponseEntity<FoundItemDto> registerFoundItem(@Valid @RequestBody FoundItemDto dto, HttpSession session, HttpServletRequest request) {
        UserDto loggedIn = authUtil.getAuthenticatedUser(session, request);
        if (loggedIn == null) {
            throw new com.example.lostfound.exception.UnauthorizedException("You must be logged in to register a found item. Please login or register.");
        }
        FoundItemDto created = foundItemService.registerFoundItem(dto, loggedIn.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all registered found items")
    public ResponseEntity<List<FoundItemDto>> getAllFoundItems() {
        return ResponseEntity.ok(foundItemService.getAllFoundItems());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get found item details by ID")
    public ResponseEntity<FoundItemDto> getFoundItemById(@PathVariable Long id) {
        return ResponseEntity.ok(foundItemService.getFoundItemById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get found item details by item code")
    public ResponseEntity<FoundItemDto> getFoundItemByCode(@PathVariable String code) {
        return ResponseEntity.ok(foundItemService.getFoundItemByCode(code));
    }

    @GetMapping("/my")
    @Operation(summary = "Get found items registered by current logged-in user")
    public ResponseEntity<List<FoundItemDto>> getMyFoundItems(HttpSession session, HttpServletRequest request) {
        UserDto loggedIn = authUtil.getAuthenticatedUser(session, request);
        if (loggedIn == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(foundItemService.getFoundItemsByFinder(loggedIn.getId()));
    }
}
