package com.example.lostfound.controller;

import com.example.lostfound.dto.AuthResponse;
import com.example.lostfound.dto.LoginRequest;
import com.example.lostfound.dto.RegisterRequest;
import com.example.lostfound.dto.UserDto;
import com.example.lostfound.service.UserService;
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

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Register, login, logout, and user session management")
public class AuthController {

    private final UserService userService;
    private final AuthUtil authUtil;

    @Autowired
    public AuthController(UserService userService, AuthUtil authUtil) {
        this.userService = userService;
        this.authUtil = authUtil;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user or administrator")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request, HttpSession session) {
        AuthResponse response = userService.register(request);
        session.setAttribute("LOGGED_IN_USER", response.getUser());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user or administrator")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        AuthResponse response = userService.login(request);
        session.setAttribute("LOGGED_IN_USER", response.getUser());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user and invalidate session")
    public ResponseEntity<AuthResponse> logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(new AuthResponse("Logged out successfully", null));
    }

    @GetMapping("/me")
    @Operation(summary = "Get currently authenticated user from session or headers")
    public ResponseEntity<UserDto> getCurrentUser(HttpSession session, HttpServletRequest request) {
        UserDto loggedIn = authUtil.getAuthenticatedUser(session, request);
        if (loggedIn == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(loggedIn);
    }
}
