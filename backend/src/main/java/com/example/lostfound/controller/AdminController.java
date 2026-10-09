package com.example.lostfound.controller;

import com.example.lostfound.dto.AdminDashboardStatsDto;
import com.example.lostfound.dto.FoundItemDto;
import com.example.lostfound.dto.LostItemDto;
import com.example.lostfound.dto.UserDto;
import com.example.lostfound.entity.ItemStatus;
import com.example.lostfound.entity.User;
import com.example.lostfound.service.AdminService;
import com.example.lostfound.service.FoundItemService;
import com.example.lostfound.service.LostItemService;
import com.example.lostfound.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Administrator", description = "Admin dashboard statistics and system management")
public class AdminController {

    private final AdminService adminService;
    private final LostItemService lostItemService;
    private final FoundItemService foundItemService;
    private final UserService userService;

    @Autowired
    public AdminController(AdminService adminService,
                           LostItemService lostItemService,
                           FoundItemService foundItemService,
                           UserService userService) {
        this.adminService = adminService;
        this.lostItemService = lostItemService;
        this.foundItemService = foundItemService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard analytics and overview metrics")
    public ResponseEntity<AdminDashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @PutMapping("/lost-items/{id}/status")
    @Operation(summary = "Admin update status of a reported lost item")
    public ResponseEntity<LostItemDto> updateLostItemStatus(@PathVariable Long id,
                                                            @RequestParam ItemStatus status,
                                                            @RequestParam(required = false, defaultValue = "Updated by Admin") String comments,
                                                            HttpSession session) {
        UserDto loggedIn = (UserDto) session.getAttribute("LOGGED_IN_USER");
        User adminUser = loggedIn != null ? userService.getUserEntityById(loggedIn.getId()) : null;
        return ResponseEntity.ok(lostItemService.updateStatus(id, status, adminUser, comments));
    }

    @PutMapping("/found-items/{id}/status")
    @Operation(summary = "Admin update status of a registered found item")
    public ResponseEntity<FoundItemDto> updateFoundItemStatus(@PathVariable Long id,
                                                             @RequestParam ItemStatus status,
                                                             @RequestParam(required = false, defaultValue = "Updated by Admin") String comments,
                                                             HttpSession session) {
        UserDto loggedIn = (UserDto) session.getAttribute("LOGGED_IN_USER");
        User adminUser = loggedIn != null ? userService.getUserEntityById(loggedIn.getId()) : null;
        return ResponseEntity.ok(foundItemService.updateStatus(id, status, adminUser, comments));
    }
}
