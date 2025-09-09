package com.asr.bank.controller;

import com.asr.bank.dto.response.TransactionDto;
import com.asr.bank.service.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/transactions/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public ResponseEntity deposit(@PathVariable Long accountId,
                                               @RequestParam BigDecimal amount) {
        transactionService.deposit(accountId, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@PathVariable Long accountId,
                                                @RequestParam BigDecimal amount) {
        transactionService.withdraw(accountId, amount);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<TransactionDto>> getTransactions(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getTransactions(accountId));
    }
}

