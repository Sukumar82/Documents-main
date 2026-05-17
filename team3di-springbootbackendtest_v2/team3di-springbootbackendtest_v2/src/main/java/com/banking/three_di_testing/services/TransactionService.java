package com.banking.three_di_testing.services;

import com.banking.three_di_testing.constants.ACTION;
import com.banking.three_di_testing.models.Account;
import com.banking.three_di_testing.models.Transaction;
import com.banking.three_di_testing.repositories.AccountRepository;
import com.banking.three_di_testing.repositories.TransactionRepository;
import com.banking.three_di_testing.utils.TransactionInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public boolean makeTransfer(TransactionInput transactionInput) {
          String sourceSortCode = transactionInput.getSourceAccount().getSortCode();
        String sourceAccountNumber = transactionInput.getSourceAccount().getAccountNumber();
        Optional<Account> sourceAccount = accountRepository
                .findBySortCodeAndAccountNumber(sourceSortCode, sourceAccountNumber);

        String targetSortCode = transactionInput.getTargetAccount().getSortCode();
        String targetAccountNumber = transactionInput.getTargetAccount().getAccountNumber();
        Optional<Account> targetAccount = accountRepository
                .findBySortCodeAndAccountNumber(targetSortCode, targetAccountNumber);

        if (sourceAccount.isPresent() && targetAccount.isPresent()) {
            if (isAmountAvailable(transactionInput.getAmount(), sourceAccount.get().getCurrentBalance())) {
                Transaction transaction = new Transaction();

                transaction.setAmount(transactionInput.getAmount());
                transaction.setSourceAccountId(sourceAccount.get().getId());
                transaction.setTargetAccountId(targetAccount.get().getId());
                transaction.setTargetOwnerName(targetAccount.get().getOwnerName());
                transaction.setInitiationDate(LocalDateTime.now());
                transaction.setCompletionDate(LocalDateTime.now());
                transaction.setReference(transactionInput.getReference());

                updateAccountBalance(sourceAccount.get(), transactionInput.getAmount(), ACTION.WITHDRAW);
                transactionRepository.save(transaction);

                return true;
            }
        }
        return false;
    }

     public void updateAccountBalance(Account account, double amount, ACTION action) {
        if (action == ACTION.WITHDRAW) {
            account.setCurrentBalance((account.getCurrentBalance() - amount));
        } else if (action == ACTION.DEPOSIT) {
            account.setCurrentBalance((account.getCurrentBalance() + amount));
        }
        accountRepository.save(account);
    }

    public boolean isAmountAvailable(double amount, double accountBalance) {
        return (accountBalance - amount) > 0;
    }

    /**
     * Aggregator method: retrieves transactions for a source account whose
     * initiationDate falls within [startDate 00:00:00, endDate 23:59:59] (both inclusive).
     *
     * Uses Java 8 LocalDate / LocalTime parsing; the derived JPA method handles
     * the BETWEEN clause so no native SQL is needed.
     *
     * @param accountId  source account ID
     * @param startDate  ISO date string "YYYY-MM-DD"  e.g. "2019-04-01"
     * @param endDate    ISO date string "YYYY-MM-DD"  e.g. "2019-06-01"
     * @return list of matching transactions ordered by initiation date
     */
    public List<Transaction> getTransactionsByDateRange(
            long accountId, String startDate, String endDate) {

        // Start of the start date (00:00:00.000000000)
        LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
        // End of the end date (23:59:59.999999999) – ensures full-day inclusivity
        LocalDateTime end   = LocalDate.parse(endDate).atTime(LocalTime.MAX);

        return transactionRepository
                .findBySourceAccountIdAndInitiationDateBetweenOrderByInitiationDate(
                        accountId, start, end);
    }

}
