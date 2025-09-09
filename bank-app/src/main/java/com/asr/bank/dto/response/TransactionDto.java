package com.asr.bank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionDto {
    Long id;
    BigDecimal amount;
    String transactionType;
    LocalDateTime timestamp;
}
