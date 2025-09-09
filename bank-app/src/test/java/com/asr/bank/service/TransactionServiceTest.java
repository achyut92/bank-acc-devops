package com.asr.bank.service;

import com.asr.bank.error.Exceptions.AccountNotFoundException;
import com.asr.bank.error.Exceptions.WithdrawalLimitExceededException;
import com.asr.bank.model.Account;
import com.asr.bank.repository.AccountRepository;
import com.asr.bank.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static com.asr.bank.mock.AccountMock.mockAccount;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deposit_shouldSaveSuccessfully() {
        Account mockAcc = mockAccount(1l);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAcc));
        assertDoesNotThrow(() -> transactionService.deposit(1L, BigDecimal.TEN));
    }

    @Test
    void deposit_shouldFailWithAccountNotFound() {
        Account mockAcc = mockAccount(1l);
        when(accountRepository.findById(2L)).thenReturn(Optional.of(mockAcc));
        assertThrows(AccountNotFoundException.class,
                () -> transactionService.deposit(1L, BigDecimal.TEN));
    }

    @Test
    void withdraw_shouldBeSuccessfull(){
        Account mockAcc = mockAccount(1l);
        mockAcc.setBalance(BigDecimal.valueOf(2000));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAcc));
        when(transactionRepository.sumWithdrawalsForToday(mockAcc, LocalDate.now()))
                .thenReturn(BigDecimal.valueOf(500));

        assertDoesNotThrow(() -> transactionService.withdraw(1L, BigDecimal.valueOf(200)));
    }

    @Test
    void withdraw_shouldThrowExceptionIfExceedsLimit() {

        Account mockAcc = mockAccount(1l);
        mockAcc.setBalance(BigDecimal.valueOf(2000));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAcc));
        when(transactionRepository.sumWithdrawalsForToday(mockAcc, LocalDate.now()))
                .thenReturn(BigDecimal.valueOf(900));

        assertThrows(WithdrawalLimitExceededException.class,
                () -> transactionService.withdraw(1L, BigDecimal.valueOf(200)));
    }

    @Test
    void withdraw_shouldFailWithAccountNotFound() {

        Account mockAcc = mockAccount(1l);
        when(accountRepository.findById(2L)).thenReturn(Optional.of(mockAcc));

        assertThrows(AccountNotFoundException.class,
                () -> transactionService.withdraw(1L, BigDecimal.valueOf(200)));
    }
}
