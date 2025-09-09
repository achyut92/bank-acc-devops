package com.asr.bank.service;

import com.asr.bank.error.Exceptions.AccountNotFoundException;
import com.asr.bank.model.Account;
import com.asr.bank.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public BigDecimal getBalance(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException())
                .getBalance();
    }

    public Account createAccount(String accountName) {
        return accountRepository.save(new Account(accountName));
    }
}

