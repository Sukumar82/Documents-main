package com.banking.three_di_testing;

import com.banking.three_di_testing.utils.AccountInput;
import com.banking.three_di_testing.utils.CreateAccountInput;
import com.banking.three_di_testing.utils.InputValidator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for InputValidator utility class.
 *
 * Pure unit tests – no Spring context loaded, runs instantly.
 *
 * Covers:
 *  - isSearchCriteriaValid : sort code + account number regex
 *  - isAccountNoValid      : account number regex
 *  - isCreateAccountCriteriaValid : blank-string checks
 */
class InputValidatorTest {

    // ─────────────────────────────────────────────────────────────
    // isSearchCriteriaValid
    // ─────────────────────────────────────────────────────────────

    @Test
    void isSearchCriteriaValid_validSortCodeAndAccountNumber_returnsTrue() {
        AccountInput input = buildAccountInput("53-68-92", "73084635");
        assertThat(InputValidator.isSearchCriteriaValid(input)).isTrue();
    }

    @Test
    void isSearchCriteriaValid_invalidSortCode_returnsFalse() {
        // Sort code must match XX-XX-XX (digits only)
        AccountInput input = buildAccountInput("ABCDEF", "73084635");
        assertThat(InputValidator.isSearchCriteriaValid(input)).isFalse();
    }

    @Test
    void isSearchCriteriaValid_invalidSortCodeMissingDashes_returnsFalse() {
        AccountInput input = buildAccountInput("536892", "73084635");
        assertThat(InputValidator.isSearchCriteriaValid(input)).isFalse();
    }

    @Test
    void isSearchCriteriaValid_invalidAccountNumberTooShort_returnsFalse() {
        // Account number must be exactly 8 digits
        AccountInput input = buildAccountInput("53-68-92", "1234567");
        assertThat(InputValidator.isSearchCriteriaValid(input)).isFalse();
    }

    @Test
    void isSearchCriteriaValid_invalidAccountNumberTooLong_returnsFalse() {
        AccountInput input = buildAccountInput("53-68-92", "123456789");
        assertThat(InputValidator.isSearchCriteriaValid(input)).isFalse();
    }

    @Test
    void isSearchCriteriaValid_invalidAccountNumberContainsLetters_returnsFalse() {
        AccountInput input = buildAccountInput("53-68-92", "7308ABCD");
        assertThat(InputValidator.isSearchCriteriaValid(input)).isFalse();
    }

    // ─────────────────────────────────────────────────────────────
    // isAccountNoValid
    // ─────────────────────────────────────────────────────────────

    @Test
    void isAccountNoValid_validEightDigits_returnsTrue() {
        assertThat(InputValidator.isAccountNoValid("73084635")).isTrue();
    }

    @Test
    void isAccountNoValid_sevenDigits_returnsFalse() {
        assertThat(InputValidator.isAccountNoValid("7308463")).isFalse();
    }

    @Test
    void isAccountNoValid_nineDigits_returnsFalse() {
        assertThat(InputValidator.isAccountNoValid("730846350")).isFalse();
    }

    @Test
    void isAccountNoValid_containsLetters_returnsFalse() {
        assertThat(InputValidator.isAccountNoValid("7308AB35")).isFalse();
    }

    @Test
    void isAccountNoValid_emptyString_returnsFalse() {
        assertThat(InputValidator.isAccountNoValid("")).isFalse();
    }

    // ─────────────────────────────────────────────────────────────
    // isCreateAccountCriteriaValid
    // ─────────────────────────────────────────────────────────────

    @Test
    void isCreateAccountCriteriaValid_validBankNameAndOwner_returnsTrue() {
        CreateAccountInput input = buildCreateAccountInput("Challenger Bank", "Paul Dragoslav");
        assertThat(InputValidator.isCreateAccountCriteriaValid(input)).isTrue();
    }

    @Test
    void isCreateAccountCriteriaValid_blankBankName_returnsFalse() {
        CreateAccountInput input = buildCreateAccountInput("   ", "Paul Dragoslav");
        assertThat(InputValidator.isCreateAccountCriteriaValid(input)).isFalse();
    }

    @Test
    void isCreateAccountCriteriaValid_blankOwnerName_returnsFalse() {
        CreateAccountInput input = buildCreateAccountInput("Challenger Bank", "");
        assertThat(InputValidator.isCreateAccountCriteriaValid(input)).isFalse();
    }

    @Test
    void isCreateAccountCriteriaValid_bothBlank_returnsFalse() {
        CreateAccountInput input = buildCreateAccountInput("", "");
        assertThat(InputValidator.isCreateAccountCriteriaValid(input)).isFalse();
    }

    // ─────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────

    private AccountInput buildAccountInput(String sortCode, String accountNumber) {
        AccountInput input = new AccountInput();
        input.setSortCode(sortCode);
        input.setAccountNumber(accountNumber);
        return input;
    }

    private CreateAccountInput buildCreateAccountInput(String bankName, String ownerName) {
        CreateAccountInput input = new CreateAccountInput();
        input.setBankName(bankName);
        input.setOwnerName(ownerName);
        return input;
    }
}
