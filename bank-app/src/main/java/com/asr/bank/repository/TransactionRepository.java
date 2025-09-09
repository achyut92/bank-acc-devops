package com.asr.bank.repository;

import com.asr.bank.model.Account;
import com.asr.bank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountId(Long accountId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) " +
            "FROM Transaction t " +
            "WHERE t.account = :account " +
            "AND t.type = 'DEBIT' " +
            "AND DATE(t.timestamp) = :date")
    BigDecimal sumWithdrawalsForToday(Account account, LocalDate date);
}
