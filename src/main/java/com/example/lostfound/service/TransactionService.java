package com.example.lostfound.service;

import com.example.lostfound.dto.TransactionDto;
import com.example.lostfound.entity.Transaction;
import com.example.lostfound.entity.User;
import com.example.lostfound.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction recordTransaction(String itemType, Long itemId, String itemCode, User user,
                                          String action, String previousStatus, String newStatus,
                                          String adminOrUserResponsible, String qrVerificationResult,
                                          String claimResult, String notes) {
        Transaction t = new Transaction();
        t.setTransactionCode("TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        t.setItemType(itemType);
        t.setItemId(itemId);
        t.setItemCode(itemCode);
        t.setUser(user);
        t.setAction(action);
        t.setPreviousStatus(previousStatus);
        t.setNewStatus(newStatus);
        t.setAdminOrUserResponsible(adminOrUserResponsible != null ? adminOrUserResponsible : (user != null ? user.getFullName() : "System"));
        t.setQrVerificationResult(qrVerificationResult);
        t.setClaimResult(claimResult);
        t.setNotes(notes);

        return transactionRepository.save(t);
    }

    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.findAllByOrderByTimestampDesc().stream()
                .map(TransactionDto::new)
                .collect(Collectors.toList());
    }

    public List<TransactionDto> getTransactionsByUser(User user) {
        return transactionRepository.findByUserOrderByTimestampDesc(user).stream()
                .map(TransactionDto::new)
                .collect(Collectors.toList());
    }

    public List<TransactionDto> getTransactionsByItemCode(String itemCode) {
        return transactionRepository.findByItemCodeOrderByTimestampDesc(itemCode).stream()
                .map(TransactionDto::new)
                .collect(Collectors.toList());
    }
}
