package com.banking.three_di_testing.utils;

/**
 * DTO for the /api/v1/alltransactions endpoint.
 *
 * Extends AccountInput to inherit sortCode and accountNumber with their
 * @NotBlank validators, so InputValidator.isSearchCriteriaValid() works
 * via polymorphism without any changes to InputValidator.
 *
 * startDate and endDate are optional ISO-8601 date strings (YYYY-MM-DD).
 * When both are supplied the backend filters transactions whose initiationDate
 * falls within [startDate 00:00:00, endDate 23:59:59] – both ends inclusive.
 */
public class AllTransactionsInput extends AccountInput {

    // Format: YYYY-MM-DD  e.g. "2019-04-01"
    private String startDate;

    // Format: YYYY-MM-DD  e.g. "2019-06-01"
    private String endDate;

    public AllTransactionsInput() {}

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "AllTransactionsInput{" +
                "sortCode='" + getSortCode() + '\'' +
                ", accountNumber='" + getAccountNumber() + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}
