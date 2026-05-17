package com.banking.three_di_testing;

import com.banking.three_di_testing.models.Account;
import com.banking.three_di_testing.models.Transaction;
import com.banking.three_di_testing.services.AccountService;
import com.banking.three_di_testing.services.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.banking.three_di_testing.controller.TransactionRestController;

/**
 * Controller-layer tests for TransactionRestController using MockMvc.
 *
 * @WebMvcTest loads only the web layer (controller + filters).
 * AccountService and TransactionService are replaced with Mockito mocks.
 * No database or full Spring context is needed.
 *
 * Covers the /api/v1/alltransactions endpoint:
 *  - Valid request without dates → returns all transactions
 *  - Valid request with dates    → returns filtered transactions
 *  - Invalid sort code           → 400 BAD REQUEST
 *  - Account not found           → returns NO_ACCOUNT_FOUND message
 */
@WebMvcTest(TransactionRestController.class)
class TransactionRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @MockBean
    private TransactionService transactionService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        Transaction t1 = new Transaction();
        t1.setId(1L);
        t1.setSourceAccountId(1L);
        t1.setTargetAccountId(2L);
        t1.setAmount(50.00);
        t1.setInitiationDate(LocalDateTime.of(2019, 4, 1, 10, 30));

        testAccount = new Account(1L, "53-68-92", "73084635", 1071.78,
                "Challenger Bank", "Paul Dragoslav", List.of(t1));
    }

    // ─────────────────────────────────────────────────────────────
    // GET all transactions (no date filter)
    // ─────────────────────────────────────────────────────────────

    @Test
    void getAllTransactions_withoutDates_returnsAccount() throws Exception {
        when(accountService.findTransactionsBySourceCodeAndAccountNumber("73084635", "53-68-92"))
                .thenReturn(testAccount);

        Map<String, Object> body = new HashMap<>();
        body.put("sortCode", "53-68-92");
        body.put("accountNumber", "73084635");
        // no startDate / endDate

        mockMvc.perform(put("/api/v1/alltransactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sortCode").value("53-68-92"))
                .andExpect(jsonPath("$.accountNumber").value("73084635"))
                .andExpect(jsonPath("$.currentBalance").value(1071.78));
    }

    // ─────────────────────────────────────────────────────────────
    // GET transactions with date range filter
    // ─────────────────────────────────────────────────────────────

    @Test
    void getAllTransactions_withDateRange_returnsFilteredAccount() throws Exception {
        when(accountService.findTransactionsByDateRange(
                eq("53-68-92"), eq("73084635"),
                eq("2019-04-01"), eq("2019-06-01")))
                .thenReturn(testAccount);

        Map<String, Object> body = new HashMap<>();
        body.put("sortCode", "53-68-92");
        body.put("accountNumber", "73084635");
        body.put("startDate", "2019-04-01");
        body.put("endDate", "2019-06-01");

        mockMvc.perform(put("/api/v1/alltransactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sortCode").value("53-68-92"))
                .andExpect(jsonPath("$.currentBalance").value(1071.78));
    }

    // ─────────────────────────────────────────────────────────────
    // Invalid sort code → 400
    // ─────────────────────────────────────────────────────────────

    @Test
    void getAllTransactions_invalidSortCode_returnsBadRequest() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("sortCode", "INVALID");   // does not match XX-XX-XX pattern
        body.put("accountNumber", "73084635");

        mockMvc.perform(put("/api/v1/alltransactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    // ─────────────────────────────────────────────────────────────
    // Account not found → returns NO_ACCOUNT_FOUND message
    // ─────────────────────────────────────────────────────────────

    @Test
    void getAllTransactions_accountNotFound_returnsNoAccountFoundMessage() throws Exception {
        when(accountService.findTransactionsBySourceCodeAndAccountNumber("99999999", "99-99-99"))
                .thenReturn(null);

        Map<String, Object> body = new HashMap<>();
        body.put("sortCode", "99-99-99");
        body.put("accountNumber", "99999999");

        mockMvc.perform(put("/api/v1/alltransactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "Unable to find an account matching this sort code and account number")));
    }

    // ─────────────────────────────────────────────────────────────
    // Only startDate provided (no endDate) → treated as no date filter
    // ─────────────────────────────────────────────────────────────

    @Test
    void getAllTransactions_onlyStartDateProvided_treatsAsNoDatesAndReturnsAll() throws Exception {
        when(accountService.findTransactionsBySourceCodeAndAccountNumber("73084635", "53-68-92"))
                .thenReturn(testAccount);

        Map<String, Object> body = new HashMap<>();
        body.put("sortCode", "53-68-92");
        body.put("accountNumber", "73084635");
        body.put("startDate", "2019-04-01");
        // endDate intentionally omitted → both dates must be present to filter

        mockMvc.perform(put("/api/v1/alltransactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentBalance").value(1071.78));
    }
}
