error id: file:///D:/Bussines/Assignments/BSReddy/Documents-main/Documents-main/team3di-springbootbackendtest_v2/team3di-springbootbackendtest_v2/src/main/java/com/banking/three_di_testing/models/Account.java:java/util/List#
file:///D:/Bussines/Assignments/BSReddy/Documents-main/Documents-main/team3di-springbootbackendtest_v2/team3di-springbootbackendtest_v2/src/main/java/com/banking/three_di_testing/models/Account.java
empty definition using pc, found symbol in pc: java/util/List#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 91
uri: file:///D:/Bussines/Assignments/BSReddy/Documents-main/Documents-main/team3di-springbootbackendtest_v2/team3di-springbootbackendtest_v2/src/main/java/com/banking/three_di_testing/models/Account.java
text:
```scala
package com.banking.three_di_testing.models;

import javax.persistence.*;
import java.util.@@List;

@Entity
@Table(name = "account", schema = "online_bank")
public class Account {

    @Id @GeneratedValue
    private long id;

    private String sortCode;

    private String accountNumber;

    private double currentBalance;

    private String bankName;

    private String ownerName;

    private transient List<Transaction> transactions;

    protected Account() {}
    public Account(String bankName, String ownerName, String generateSortCode, String generateAccountNumber, double currentBalance) {
        this.sortCode = generateSortCode;
        this.accountNumber = generateAccountNumber;
        this.currentBalance = currentBalance;
        this.bankName = bankName;
        this.ownerName = ownerName;
    }
    public Account(long id, String sortCode, String accountNumber, double currentBalance, String bankName, String ownerName) {
        this.id = id;
        this.sortCode = sortCode;
        this.accountNumber = accountNumber;
        this.currentBalance = currentBalance;
        this.bankName = bankName;
        this.ownerName = ownerName;
    }

    public Account(long id, String sortCode, String accountNumber, double currentBalance, String bankName, String ownerName, List<Transaction> transactions) {
        this.id = id;
        this.sortCode = sortCode;
        this.accountNumber = accountNumber;
        this.currentBalance = currentBalance;
        this.bankName = bankName;
        this.ownerName = ownerName;
        this.transactions = transactions;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getSortCode() {
        return sortCode;
    }
    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public double getCurrentBalance() {
        return currentBalance;
    }
    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }
    public String getOwnerName() {
        return ownerName;
    }
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    public String getBankName() {
        return bankName;
    }
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    public List<Transaction> getTransactions() {
        return transactions;
    }
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", sortCode='" + sortCode + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", currentBalance=" + currentBalance +
                ", bankName='" + bankName + '\'' +
                ", ownerName='" + ownerName + '\'' +
                '}';
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: java/util/List#