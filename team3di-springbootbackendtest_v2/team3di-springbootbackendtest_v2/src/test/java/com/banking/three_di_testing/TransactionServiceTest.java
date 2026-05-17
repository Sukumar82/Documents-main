package com.banking.three_di_testing;

import com.banking.three_di_testing.constants.ACTION;
import com.banking.three_di_testing.models.Account;
import com.banking.three_di_testing.models.Transaction;
import com.banking.three_di_testing.repositories.AccountRepository;
import com.banking.three_di_testing.repositories.TransactionRepository;
import com.banking.three_di_testing.services.TransactionService;
import com.banking.three_di_testing.utils.AccountInput;
import com.banking.three_di_testing.utils.TransactionInput;
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
import static org.mockito.Mockito.*;

/**
 * Unit tests for TransactionService (non-date-range logic).
 *
 * Date-range tests live in TransactionDateRangeTest.
 * This file covers: makeTransfer, isAmountAvailable, updateAccountBalance.
 *
 * Uses Mockito – no Spring context, no database.
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account sourceAccount;
    private Account targetAccount;

    @BeforeEach
    void setUp() {
        sourceAccount = new Account(1L, "53-68-92", "73084635", 1071.78, "Challenger Bank", "Paul Dragoslav");
        targetAccount = new Account(2L, "12-34-56", "12345678", 500.00, "Challenger Bank", "Scrooge McDuck");
    }

    // ─────────────────────────────────────────────────────────────
    // isAmountAvailable
    // ─────────────────────────────────────────────────────────────

    @Test
    void isAmountAvailable_sufficientBalance_returnsTrue() {
        // 1071.78 - 100.00 = 971.78 > 0
        assertThat(transactionService.isAmountAvailable(100.00, 1071.78)).isTrue();
    }

    @Test
    void isAmountAvailable_insufficientBalance_returnsFalse() {
        // 50.00 - 100.00 = -50.00 which is NOT > 0
        assertThat(transactionService.isAmountAvailable(100.00, 50.00)).isFalse();
    }

    @Test
    void isAmountAvailable_exactBalance_returnsFalse() {
        // 100.00 - 100.00 = 0 which is NOT > 0 (strict greater-than check)
        assertThat(transactionService.isAmountAvailable(100.00, 100.00)).isFalse();
    }

    // ─────────────────────────────────────────────────────────────
    // updateAccountBalance
    // ─────────────────────────────────────────────────────────────

    @Test
    void updateAccountBalance_withdraw_decreasesBalance() {
        double originalBalance = sourceAccount.getCurrentBalance(); // 1071.78

        transactionService.updateAccountBalance(sourceAccount, 100.00, ACTION.WITHDRAW);

        assertThat(sourceAccount.getCurrentBalance()).isEqualTo(originalBalance - 100.00);
        verify(accountRepository, times(1)).save(sourceAccount);
    }

    @Test
    void updateAccountBalance_deposit_increasesBalance() {
        double originalBalance = targetAccount.getCurrentBalance(); // 500.00

        transactionService.updateAccountBalance(targetAccount, 200.00, ACTION.DEPOSIT);

        assertThat(targetAccount.getCurrentBalance()).isEqualTo(originalBalance + 200.00);
        verify(accountRepository, times(1)).save(targetAccount);
    }

    // ─────────────────────────────────────────────────────────────
    // makeTransfer
    // ─────────────────────────────────────────────────────────────

    @Test
    void makeTransfer_success_returnsTrueAndSavesTransaction() {
        when(accountRepository.findBySortCodeAndAccountNumber("53-68-92", "73084635"))
                .thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findBySortCodeAndAccountNumber("12-34-56", "12345678"))
                .thenReturn(Optional.of(targetAccount));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        TransactionInput input = buildTransactionInput(
                "53-68-92", "73084635",
                "12-34-56", "12345678",
                100.00, "Test transfer");

        boolean result = transactionService.makeTransfer(input);

        assertThat(result).isTrue();
        // Source account balance should have been reduced
        assertThat(sourceAccount.getCurrentBalance()).isEqualTo(1071.78 - 100.00);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void makeTransfer_insufficientBalance_returnsFalse() {
        // Source account has only 50.00 but trying to transfer 100.00
        sourceAccount = new Account(1L, "53-68-92", "73084635", 50.00, "Challenger Bank", "Paul Dragoslav");

        when(accountRepository.findBySortCodeAndAccountNumber("53-68-92", "73084635"))
                .thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findBySortCodeAndAccountNumber("12-34-56", "12345678"))
                .thenReturn(Optional.of(targetAccount));

        TransactionInput input = buildTransactionInput(
                "53-68-92", "73084635",
                "12-34-56", "12345678",
                100.00, "Test transfer");

        boolean result = transactionService.makeTransfer(input);

        assertThat(result).isFalse();
        // No transaction should have been saved
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void makeTransfer_sourceAccountNotFound_returnsFalse() {
        when(accountRepository.findBySortCodeAndAccountNumber("00-00-00", "00000000"))
                .thenReturn(Optional.empty());
        when(accountRepository.findBySortCodeAndAccountNumber("12-34-56", "12345678"))
                .thenReturn(Optional.of(targetAccount));

        TransactionInput input = buildTransactionInput(
                "00-00-00", "00000000",
                "12-34-56", "12345678",
                100.00, "Test transfer");

        boolean result = transactionService.makeTransfer(input);

        assertThat(result).isFalse();
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void makeTransfer_targetAccountNotFound_returnsFalse() {
        when(accountRepository.findBySortCodeAndAccountNumber("53-68-92", "73084635"))
                .thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findBySortCodeAndAccountNumber("00-00-00", "00000000"))
                .thenReturn(Optional.empty());

        TransactionInput input = buildTransactionInput(
                "53-68-92", "73084635",
                "00-00-00", "00000000",
                100.00, "Test transfer");

        boolean result = transactionService.makeTransfer(input);

        assertThat(result).isFalse();
        verify(transactionRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────────────────────

    private TransactionInput buildTransactionInput(
            String sourceSortCode, String sourceAccountNumber,
            String targetSortCode, String targetAccountNumber,
            double amount, String reference) {

        AccountInput source = new AccountInput();
        source.setSortCode(sourceSortCode);
        source.setAccountNumber(sourceAccountNumber);

        AccountInput target = new AccountInput();
        target.setSortCode(targetSortCode);
        target.setAccountNumber(targetAccountNumber);

        TransactionInput input = new TransactionInput();
        input.setSourceAccount(source);
        input.setTargetAccount(target);
        input.setAmount(amount);
        input.setReference(reference);
        return input;
    }
}
