package com.asr.bank.mock;

import com.asr.bank.dto.response.TransactionDto;
import com.asr.bank.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionMock {

    public List<TransactionDto> mockTransactionDtos() {
        List<TransactionDto> transactionList = new ArrayList<>();
        transactionList.add(new TransactionDto(
                1L, BigDecimal.valueOf(100.00), TransactionType.CREDIT.name(), LocalDateTime.now()
        ));
        transactionList.add(new TransactionDto(
                2L, BigDecimal.valueOf(10.00), TransactionType.DEBIT.name(), LocalDateTime.now()
        ));
        return transactionList;
    }
}
