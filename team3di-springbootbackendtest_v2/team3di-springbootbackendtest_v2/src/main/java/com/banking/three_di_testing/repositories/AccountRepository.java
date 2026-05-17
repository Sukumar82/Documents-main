package com.banking.three_di_testing.repositories;

import com.banking.three_di_testing.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findBySortCodeAndAccountNumber(String sortCode, String accountNumber);
    Optional<Account> findByAccountNumber(String accountNumber);

    //    @Query(value = "SELECT  a.sort_code, a.account_number, t.amount,t.initiation_date FROM account a  JOIN  transaction t on t.source_account_id=a.id WHERE a.account_number=:accountNumber AND a.sort_code= :sortCode ORDER BY t.initiation_date DESC", nativeQuery = true)
//    List<Account> findBySortCodeAndAccountNumber(String accountNumber, String sortCode);

}
