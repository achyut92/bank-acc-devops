package com.asr.bank.mapper;

import com.asr.bank.dto.response.TransactionDto;
import com.asr.bank.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionMapper {
    public List<TransactionDto> toDto(List<Transaction> transactions){
        return transactions.stream().map(txn -> new TransactionDto(txn.getId(), txn.getAmount(),
                txn.getType().name(), txn.getTimestamp())).collect(Collectors.toList());
    }
}
