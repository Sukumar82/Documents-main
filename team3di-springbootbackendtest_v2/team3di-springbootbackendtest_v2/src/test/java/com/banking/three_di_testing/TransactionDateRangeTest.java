package com.banking.three_di_testing;

import com.banking.three_di_testing.models.Account;
import com.banking.three_di_testing.models.Transaction;
import com.banking.three_di_testing.repositories.AccountRepository;
import com.banking.three_di_testing.repositories.TransactionRepository;
import com.banking.three_di_testing.services.AccountService;
import com.banking.three_di_testing.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for date-range transaction filtering.
 *
 * Uses Mockito to isolate service logic from the database.
 * No Spring context is loaded – tests run fast.
 */
@ExtendWith(MockitoExtension.class)
class TransactionDateRangeTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;
    private Transaction aprilTransaction;
    private Transaction mayTransaction;
    private Transaction juneTransaction;

    @BeforeEach
    void setUp() {
        // Account matches the sample data in schema.sql
        testAccount = new Account(1L, "53-68-92", "73084635", 1071.78, "Challenger Bank", "Paul Dragoslav");

        // Transactions matching schema.sql dates
        aprilTransaction = buildTransaction(1L, 1L, "2019-04-01T10:30:00");
        mayTransaction   = buildTransaction(2L, 1L, "2019-05-01T10:30:00");
        juneTransaction  = buildTransaction(4L, 1L, "2019-06-01T09:30:00");
    }

    // ─────────────────────────────────────────────────────────────
    // TransactionService tests
    // ─────────────────────────────────────────────────────────────

    @Test
    void getTransactionsByDateRange_returnsOnlyTransactionsWithinRange() {
        // Only April + May fall within 2019-04-01 to 2019-05-31
        when(transactionRepository.findBySourceAccountIdAndInitiationDateBetweenOrderByInitiationDate(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(aprilTransaction, mayTransaction));

        List<Transaction> result = transactionService.getTransactionsByDateRange(1L, "2019-04-01", "2019-05-31");

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(aprilTransaction, mayTransaction);
    }

    @Test
    void getTransactionsByDateRange_startDateBoundaryIsInclusive() {
        // startDate == exact date of aprilTransaction – should be included
        when(transactionRepository.findBySourceAccountIdAndInitiationDateBetweenOrderByInitiationDate(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(aprilTransaction));

        List<Transaction> result = transactionService.getTransactionsByDateRange(1L, "2019-04-01", "2019-04-30");

        // The April record (2019-04-01 10:30) must be included when startDate = 2019-04-01
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void getTransactionsByDateRange_endDateBoundaryIsInclusive() {
        // endDate == exact date of juneTransaction – should be included
        when(transactionRepository.findBySourceAccountIdAndInitiationDateBetweenOrderByInitiationDate(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(aprilTransaction, mayTransaction, juneTransaction));

        List<Transaction> result = transactionService.getTransactionsByDateRange(1L, "2019-04-01", "2019-06-01");

        assertThat(result).hasSize(3);
        assertThat(result).contains(juneTransaction);
    }

    @Test
    void getTransactionsByDateRange_returnsEmptyListWhenNoMatchFound() {
        when(transactionRepository.findBySourceAccountIdAndInitiationDateBetweenOrderByInitiationDate(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        // Date range far in the future – no records expected
        List<Transaction> result = transactionService.getTransactionsByDateRange(1L, "2025-01-01", "2025-12-31");

        assertThat(result).isEmpty();
    }

    // ─────────────────────────────────────────────────────────────
    // AccountService tests
    // ─────────────────────────────────────────────────────────────

    @Test
    void findTransactionsByDateRange_attachesFilteredTransactionsToAccount() {
        when(accountRepository.findBySortCodeAndAccountNumber("53-68-92", "73084635"))
                .thenReturn(Optional.of(testAccount));
        when(transactionRepository.findBySourceAccountIdAndInitiationDateBetweenOrderByInitiationDate(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(aprilTransaction, mayTransaction));

        Account result = accountService.findTransactionsByDateRange(
                "53-68-92", "73084635", "2019-04-01", "2019-05-31");

        // Account details are present
        assertThat(result).isNotNull();
        assertThat(result.getCurrentBalance()).isEqualTo(1071.78);

        // Filtered transactions are attached
        assertThat(result.getTransactions()).hasSize(2);
    }

    @Test
    void findTransactionsByDateRange_returnsNullWhenAccountNotFound() {
        when(accountRepository.findBySortCodeAndAccountNumber("00-00-00", "00000000"))
                .thenReturn(Optional.empty());

        Account result = accountService.findTransactionsByDateRange(
                "00-00-00", "00000000", "2019-04-01", "2019-06-01");

        assertThat(result).isNull();
    }

    // ─────────────────────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────────────────────

    private Transaction buildTransaction(long id, long sourceAccountId, String initiationDate) {
        Transaction t = new Transaction();
        t.setId(id);
        t.setSourceAccountId(sourceAccountId);
        t.setTargetAccountId(2L);
        t.setTargetOwnerName("Scrooge McDuck");
        t.setAmount(100.00);
        t.setInitiationDate(LocalDateTime.parse(initiationDate));
        return t;
    }
}
