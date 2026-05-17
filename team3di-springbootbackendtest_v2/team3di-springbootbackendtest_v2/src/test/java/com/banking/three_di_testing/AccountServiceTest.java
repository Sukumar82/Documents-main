package com.banking.three_di_testing;

import com.banking.three_di_testing.models.Account;
import com.banking.three_di_testing.models.Transaction;
import com.banking.three_di_testing.repositories.AccountRepository;
import com.banking.three_di_testing.repositories.TransactionRepository;
import com.banking.three_di_testing.services.AccountService;
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
 * Unit tests for AccountService.
 *
 * Uses Mockito to mock both AccountRepository and TransactionRepository.
 * No Spring context is loaded – tests run fast.
 *
 * Covers:
 *  - getAccount(sortCode, accountNumber) : found / not found
 *  - getAccount(accountNumber)           : found / not found
 *  - findTransactionsBySourceCodeAndAccountNumber : attaches transactions
 *  - findTransactionsByDateRange         : found with transactions / account not found
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;
    private Transaction sampleTransaction;

    @BeforeEach
    void setUp() {
        testAccount = new Account(1L, "53-68-92", "73084635", 1071.78, "Challenger Bank", "Paul Dragoslav");

        sampleTransaction = new Transaction();
        sampleTransaction.setId(1L);
        sampleTransaction.setSourceAccountId(1L);
        sampleTransaction.setTargetAccountId(2L);
        sampleTransaction.setAmount(100.00);
        sampleTransaction.setInitiationDate(LocalDateTime.of(2019, 4, 1, 10, 30));
    }

    // ─────────────────────────────────────────────────────────────
    // getAccount(sortCode, accountNumber)
    // ─────────────────────────────────────────────────────────────

    @Test
    void getAccount_bySortCodeAndAccountNumber_found_returnsAccountWithTransactions() {
        when(accountRepository.findBySortCodeAndAccountNumber("53-68-92", "73084635"))
                .thenReturn(Optional.of(testAccount));
        when(transactionRepository.findBySourceAccountIdOrderByInitiationDate(1L))
                .thenReturn(List.of(sampleTransaction));

        Account result = accountService.getAccount("53-68-92", "73084635");

        assertThat(result).isNotNull();
        assertThat(result.getSortCode()).isEqualTo("53-68-92");
        assertThat(result.getAccountNumber()).isEqualTo("73084635");
        assertThat(result.getCurrentBalance()).isEqualTo(1071.78);
        assertThat(result.getTransactions()).hasSize(1);
    }

    @Test
    void getAccount_bySortCodeAndAccountNumber_notFound_returnsNull() {
        when(accountRepository.findBySortCodeAndAccountNumber("00-00-00", "00000000"))
                .thenReturn(Optional.empty());

        Account result = accountService.getAccount("00-00-00", "00000000");

        assertThat(result).isNull();
    }

    // ─────────────────────────────────────────────────────────────
    // getAccount(accountNumber)
    // ─────────────────────────────────────────────────────────────

    @Test
    void getAccount_byAccountNumber_found_returnsAccount() {
        when(accountRepository.findByAccountNumber("73084635"))
                .thenReturn(Optional.of(testAccount));

        Account result = accountService.getAccount("73084635");

        assertThat(result).isNotNull();
        assertThat(result.getAccountNumber()).isEqualTo("73084635");
    }

    @Test
    void getAccount_byAccountNumber_notFound_returnsNull() {
        when(accountRepository.findByAccountNumber("00000000"))
                .thenReturn(Optional.empty());

        Account result = accountService.getAccount("00000000");

        assertThat(result).isNull();
    }

    // ─────────────────────────────────────────────────────────────
    // findTransactionsBySourceCodeAndAccountNumber
    // ─────────────────────────────────────────────────────────────

    @Test
    void findTransactionsBySourceCodeAndAccountNumber_found_attachesAllTransactions() {
        when(accountRepository.findBySortCodeAndAccountNumber("53-68-92", "73084635"))
                .thenReturn(Optional.of(testAccount));
        when(transactionRepository.findBySourceAccountIdOrderByInitiationDate(1L))
                .thenReturn(List.of(sampleTransaction));

        Account result = accountService.findTransactionsBySourceCodeAndAccountNumber("73084635", "53-68-92");

        assertThat(result).isNotNull();
        assertThat(result.getTransactions()).hasSize(1);
        assertThat(result.getTransactions().get(0).getId()).isEqualTo(1L);
    }

    @Test
    void findTransactionsBySourceCodeAndAccountNumber_accountNotFound_returnsNull() {
        when(accountRepository.findBySortCodeAndAccountNumber("00-00-00", "00000000"))
                .thenReturn(Optional.empty());

        Account result = accountService.findTransactionsBySourceCodeAndAccountNumber("00000000", "00-00-00");

        assertThat(result).isNull();
    }

    // ─────────────────────────────────────────────────────────────
    // findTransactionsByDateRange
    // ─────────────────────────────────────────────────────────────

    @Test
    void findTransactionsByDateRange_found_returnsAccountWithFilteredTransactions() {
        when(accountRepository.findBySortCodeAndAccountNumber("53-68-92", "73084635"))
                .thenReturn(Optional.of(testAccount));
        when(transactionRepository.findBySourceAccountIdAndInitiationDateBetweenOrderByInitiationDate(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(sampleTransaction));

        Account result = accountService.findTransactionsByDateRange(
                "53-68-92", "73084635", "2019-04-01", "2019-04-30");

        assertThat(result).isNotNull();
        assertThat(result.getCurrentBalance()).isEqualTo(1071.78);
        assertThat(result.getTransactions()).hasSize(1);
    }

    @Test
    void findTransactionsByDateRange_accountNotFound_returnsNull() {
        when(accountRepository.findBySortCodeAndAccountNumber("00-00-00", "00000000"))
                .thenReturn(Optional.empty());

        Account result = accountService.findTransactionsByDateRange(
                "00-00-00", "00000000", "2019-04-01", "2019-06-01");

        assertThat(result).isNull();
    }

    @Test
    void findTransactionsByDateRange_noTransactionsInRange_returnsAccountWithEmptyList() {
        when(accountRepository.findBySortCodeAndAccountNumber("53-68-92", "73084635"))
                .thenReturn(Optional.of(testAccount));
        when(transactionRepository.findBySourceAccountIdAndInitiationDateBetweenOrderByInitiationDate(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        Account result = accountService.findTransactionsByDateRange(
                "53-68-92", "73084635", "2025-01-01", "2025-12-31");

        assertThat(result).isNotNull();
        assertThat(result.getTransactions()).isEmpty();
    }
}
