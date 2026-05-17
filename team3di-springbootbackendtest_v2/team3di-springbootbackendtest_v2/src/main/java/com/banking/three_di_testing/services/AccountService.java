package com.banking.three_di_testing.services;

import com.banking.three_di_testing.models.Account;
import com.banking.three_di_testing.repositories.AccountRepository;
import com.banking.three_di_testing.repositories.TransactionRepository;
import com.banking.three_di_testing.utils.CodeGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository,
                          TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Account getAccount(String sortCode, String accountNumber) {
        Optional<Account> account = accountRepository
                .findBySortCodeAndAccountNumber(sortCode, accountNumber);

        account.ifPresent(value ->
                value.setTransactions(transactionRepository
                        .findBySourceAccountIdOrderByInitiationDate(value.getId())));

        return account.orElse(null);
    }

    public Account getAccount(String accountNumber) {
        Optional<Account> account = accountRepository
                .findByAccountNumber(accountNumber);

        return account.orElse(null);
    }

    public Account createAccount(String bankName, String ownerName) {
        CodeGenerator codeGenerator = new CodeGenerator();
        Account newAccount = new Account(bankName, ownerName, codeGenerator.generateSortCode(), codeGenerator.generateAccountNumber(), 0.00);
        return accountRepository.save(newAccount);
    }

    public Account findTransactionsBySourceCodeAndAccountNumber(String accountNumber, String sortCode) {
        Optional<Account> account = accountRepository
                .findBySortCodeAndAccountNumber(sortCode, accountNumber);

        // Attach all transactions ordered by date (no date filter)
        account.ifPresent(value ->
                value.setTransactions(transactionRepository
                        .findBySourceAccountIdOrderByInitiationDate(value.getId())));

        return account.orElse(null);
    }

    /**
     * Aggregator: looks up the account then filters its transactions by
     * initiationDate range (both start and end dates are inclusive).
     *
     * Start date maps to 00:00:00 and end date maps to 23:59:59 so that
     * records whose initiationDate exactly matches either boundary are included.
     *
     * E.g. startDate = "2019-04-01" includes records with initiationDate 2019-04-01 10:30.
     *
     * @param sortCode      account sort code  e.g. "53-68-92"
     * @param accountNumber account number     e.g. "73084635"
     * @param startDate     ISO date string    e.g. "2019-04-01"
     * @param endDate       ISO date string    e.g. "2019-06-01"
     * @return Account with filtered transactions, or null if not found
     */
    public Account findTransactionsByDateRange(
            String sortCode, String accountNumber,
            String startDate, String endDate) {

        Optional<Account> account = accountRepository
                .findBySortCodeAndAccountNumber(sortCode, accountNumber);

        account.ifPresent(value -> {
            // Convert ISO date strings to LocalDateTime bounds
            LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
            LocalDateTime end   = LocalDate.parse(endDate).atTime(LocalTime.MAX);

            // Use the derived JPA method for an indexed date-range query
            value.setTransactions(transactionRepository
                    .findBySourceAccountIdAndInitiationDateBetweenOrderByInitiationDate(
                            value.getId(), start, end));
        });

        return account.orElse(null);
    }
}
