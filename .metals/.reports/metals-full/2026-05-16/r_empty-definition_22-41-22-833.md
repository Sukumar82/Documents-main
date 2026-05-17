error id: file:///D:/Bussines/Assignments/BSReddy/Documents-main/Documents-main/team3di-springbootbackendtest_v2/team3di-springbootbackendtest_v2/src/main/java/com/banking/three_di_testing/controller/TransactionRestController.java:_empty_/AccountService#findTransactionsBySourceCodeAndAccountNumber#
file:///D:/Bussines/Assignments/BSReddy/Documents-main/Documents-main/team3di-springbootbackendtest_v2/team3di-springbootbackendtest_v2/src/main/java/com/banking/three_di_testing/controller/TransactionRestController.java
empty definition using pc, found symbol in pc: _empty_/AccountService#findTransactionsBySourceCodeAndAccountNumber#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 7427
uri: file:///D:/Bussines/Assignments/BSReddy/Documents-main/Documents-main/team3di-springbootbackendtest_v2/team3di-springbootbackendtest_v2/src/main/java/com/banking/three_di_testing/controller/TransactionRestController.java
text:
```scala
package com.banking.three_di_testing.controller;

import com.banking.three_di_testing.constants.ACTION;
import com.banking.three_di_testing.constants.constants;
import com.banking.three_di_testing.models.Account;
import com.banking.three_di_testing.models.Transaction;
import com.banking.three_di_testing.services.AccountService;
import com.banking.three_di_testing.services.TransactionService;
import com.banking.three_di_testing.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.banking.three_di_testing.constants.constants.*;

@RestController
@RequestMapping("api/v1")
public class TransactionRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionRestController.class);

    private final AccountService accountService;
    private final TransactionService transactionService;

    @Autowired
    public TransactionRestController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @PostMapping(value = "/transactions",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> makeTransfer(
            @Valid @RequestBody TransactionInput transactionInput) {
        if (InputValidator.isSearchTransactionValid(transactionInput)) {
//            new Thread(() -> transactionService.makeTransfer(transactionInput));
            boolean isComplete = transactionService.makeTransfer(transactionInput);
            return new ResponseEntity<>(isComplete, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(INVALID_TRANSACTION, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/withdraw",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> withdraw(
            @Valid @RequestBody WithdrawInput withdrawInput) {
        LOGGER.debug("Triggered AccountRestController.withdrawInput");

        // Validate input
        if (InputValidator.isSearchCriteriaValid(withdrawInput)) {
            // Attempt to retrieve the account information
            Account account = accountService.getAccount(
                    withdrawInput.getSortCode(), withdrawInput.getAccountNumber());

            // Return the account details, or warn that no account was found for given input
            if (account == null) {
                return new ResponseEntity<>(NO_ACCOUNT_FOUND, HttpStatus.OK);
            } else {
                if (transactionService.isAmountAvailable(withdrawInput.getAmount(), account.getCurrentBalance())) {
                    transactionService.updateAccountBalance(account, withdrawInput.getAmount(), ACTION.WITHDRAW);
                    return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
                }
                return new ResponseEntity<>(INSUFFICIENT_ACCOUNT_BALANCE, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(INVALID_SEARCH_CRITERIA, HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping(value = "/deposit",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deposit(
            @Valid @RequestBody DepositInput depositInput) {
        LOGGER.debug("Triggered AccountRestController.depositInput");

        // Validate input
        if (InputValidator.isAccountNoValid(depositInput.getTargetAccountNo())) {
            // Attempt to retrieve the account information
            Account account = accountService.getAccount(depositInput.getTargetAccountNo());

            // Return the account details, or warn that no account was found for given input
            if (account == null) {
                return new ResponseEntity<>(NO_ACCOUNT_FOUND, HttpStatus.OK);
            } else {
                transactionService.updateAccountBalance(account, depositInput.getAmount(), ACTION.DEPOSIT);
                return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(INVALID_SEARCH_CRITERIA, HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }



    /**
     * GET (via PUT) all transactions for an account, with optional date-range filtering.
     *
     * The request body must always contain sortCode and accountNumber.
     * Optionally supply startDate and endDate (YYYY-MM-DD) to filter by initiationDate.
     * Both boundary dates are inclusive: e.g. startDate "2019-04-01" includes records
     * with initiationDate "2019-04-01 10:30:00".
     *
     * If no dates are supplied, all transactions are returned (backward-compatible).
     *
     * REST versioning: this endpoint is versioned under /api/v1/ (path-based versioning).
     */
    @PutMapping(value = "/alltransactions")
    public ResponseEntity<?> getAllTransactions(
            @Valid @RequestBody AllTransactionsInput allTransactionsInput) {
        LOGGER.debug("Triggered TransactionRestController.getAllTransactions with: {}", allTransactionsInput);

        // Validate sort code and account number (polymorphic call via AllTransactionsInput -> AccountInput)
        if (!InputValidator.isSearchCriteriaValid(allTransactionsInput)) {
            return new ResponseEntity<>(constants.INVALID_SEARCH_CRITERIA, HttpStatus.BAD_REQUEST);
        }

        Account account;

        // Determine whether a date-range filter was provided
        boolean hasDateRange = allTransactionsInput.getStartDate() != null
                && !allTransactionsInput.getStartDate().isBlank()
                && allTransactionsInput.getEndDate() != null
                && !allTransactionsInput.getEndDate().isBlank();

        if (hasDateRange) {
            // Task 2: filter transactions whose initiationDate is within [startDate, endDate] inclusive
            account = accountService.findTransactionsByDateRange(
                    allTransactionsInput.getSortCode(),
                    allTransactionsInput.getAccountNumber(),
                    allTransactionsInput.getStartDate(),
                    allTransactionsInput.getEndDate());
        } else {
            // Fallback: return all transactions for the account (no date filter)
            account = accountService.@@findTransactionsBySourceCodeAndAccountNumber(
                    allTransactionsInput.getAccountNumber(),
                    allTransactionsInput.getSortCode());
        }

        if (account == null) {
            return new ResponseEntity<>(constants.NO_ACCOUNT_FOUND, HttpStatus.OK);
        }
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/AccountService#findTransactionsBySourceCodeAndAccountNumber#