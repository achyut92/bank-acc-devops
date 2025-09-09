package com.asr.bank.mock;

import com.asr.bank.model.Account;

import java.math.BigDecimal;

public class AccountMock {
    public static Account mockAccount(Long id) {
        Account acc = new Account();
        acc.setId(id);
        acc.setBalance(BigDecimal.ZERO);
        acc.setName("Test");
        return acc;
    }
}
