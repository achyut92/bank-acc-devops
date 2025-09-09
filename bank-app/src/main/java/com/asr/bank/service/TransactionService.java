package com.asr.bank.service;

import com.asr.bank.dto.response.TransactionDto;
import com.asr.bank.enums.TransactionType;
import com.asr.bank.error.Exceptions.AccountNotFoundException;
import com.asr.bank.error.Exceptions.WithdrawalLimitExceededException;
import com.asr.bank.mapper.TransactionMapper;
import com.asr.bank.model.Account;
import com.asr.bank.model.Transaction;
import com.asr.bank.repository.AccountRepository;
import com.asr.bank.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(AccountRepository accountRepository,
                              TransactionRepository transactionRepository,
                              TransactionMapper transactionMapper) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Transactional
    public void deposit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException());

        account.setBalance(account.getBalance().add(amount));

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(TransactionType.CREDIT);
        transaction.setAccount(account);

        transactionRepository.save(transaction);
    }

    @Transactional
    public void withdraw(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException());

        if (account.getBalance().compareTo(amount) < 0) {
            throw new WithdrawalLimitExceededException("Insufficient balance.");
        }

        BigDecimal todayWithdrawn =  transactionRepository.sumWithdrawalsForToday(account, LocalDate.now());
        if (todayWithdrawn.add(amount).compareTo(BigDecimal.valueOf(1000)) > 0) {
            throw new WithdrawalLimitExceededException("Daily withdrawal limit ($1000) exceeded. Contact bank.");
        }

        account.setBalance(account.getBalance().subtract(amount));

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(TransactionType.DEBIT);
        transaction.setAccount(account);

        transactionRepository.save(transaction);
    }

    public List<TransactionDto> getTransactions(Long accountId) {
        return transactionMapper.toDto(transactionRepository.findByAccountId(accountId));
    }
}

