package com.example.lostfound.controller;

import com.example.lostfound.dto.TransactionDto;
import com.example.lostfound.dto.UserDto;
import com.example.lostfound.entity.User;
import com.example.lostfound.service.TransactionService;
import com.example.lostfound.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions & Audit History", description = "Complete recovery log and item transaction history")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    @Autowired
    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get complete transaction and recovery audit history (Admin)")
    public ResponseEntity<List<TransactionDto>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/my")
    @Operation(summary = "Get transaction history for the logged-in user")
    public ResponseEntity<List<TransactionDto>> getMyTransactions(HttpSession session) {
        UserDto loggedIn = (UserDto) session.getAttribute("LOGGED_IN_USER");
        if (loggedIn == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.getUserEntityById(loggedIn.getId());
        return ResponseEntity.ok(transactionService.getTransactionsByUser(user));
    }

    @GetMapping("/item/{itemCode}")
    @Operation(summary = "Get transaction history for a specific lost or found item code")
    public ResponseEntity<List<TransactionDto>> getTransactionsByItemCode(@PathVariable String itemCode) {
        return ResponseEntity.ok(transactionService.getTransactionsByItemCode(itemCode));
    }
}
