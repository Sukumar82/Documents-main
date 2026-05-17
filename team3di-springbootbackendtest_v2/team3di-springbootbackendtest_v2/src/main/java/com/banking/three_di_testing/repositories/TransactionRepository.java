package com.banking.three_di_testing.repositories;

import com.banking.three_di_testing.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Returns all transactions for a given source account ordered by initiation date.
     * Used as the fallback (no date filter) path.
     */
    List<Transaction> findBySourceAccountIdOrderByInitiationDate(long id);

    /**
     * Returns transactions for a source account whose initiationDate falls
     * within [startDate, endDate] inclusive (Spring Data BETWEEN is >=/<= on both ends).
     * Results are ordered by initiation date ascending.
     *
     * An index on (source_account_id, initiation_date) would further optimise this query.
     */
    List<Transaction> findBySourceAccountIdAndInitiationDateBetweenOrderByInitiationDate(
            long sourceAccountId,
            LocalDateTime startDate,
            LocalDateTime endDate);
}
