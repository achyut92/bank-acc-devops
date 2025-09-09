package com.asr.bank.service;

import com.asr.bank.error.Exceptions.AccountNotFoundException;
import com.asr.bank.model.Account;
import com.asr.bank.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static com.asr.bank.mock.AccountMock.mockAccount;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAccount_shouldSaveSuccessfully(){
        Account mockAcc = mockAccount(1l);
        when(accountRepository.save(any(Account.class))).thenReturn(mockAcc);
        Account newAccount = accountService.createAccount("Test");
        assertNotNull(newAccount);
        assertEquals(1L, newAccount.getId());
    }

    @Test
    void getBalance_shouldSuccessfullyGetBalance(){
        Account mockAcc = mockAccount(1l);
        mockAcc.setBalance(BigDecimal.valueOf(2000));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAcc));
        BigDecimal balance = accountService.getBalance(1L);
        assertEquals(BigDecimal.valueOf(2000), balance);
    }

    @Test
    void getBalance_shouldFailWithAccountNotFound(){
        Account mockAcc = mockAccount(1l);
        mockAcc.setBalance(BigDecimal.valueOf(2000));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAcc));
        assertThrows(AccountNotFoundException.class, () -> accountService.getBalance(2L));
    }
}
