package com.example.muchbetter;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@RedisHash("User")
public class User implements Serializable {

    @NonNull
    private String id;

    @NonNull
    private BigDecimal balance;

    @NonNull
    private String currency;

    private List<Transaction> transactions;

    public List<Transaction> getTransactions() {
        if (transactions == null)
            transactions = new ArrayList<>();
        return transactions;
    }

    public void applyTransaction(Transaction transaction) throws InsifficientFundsException {
        Assert.isTrue(transaction.getCurrency().equals(getCurrency()), "Transaction currency must match user's currency");

        BigDecimal newBalance = balance.subtract(transaction.getAmount());
        if (newBalance.compareTo(BigDecimal.ZERO) < 0)
            throw new InsifficientFundsException("New balance will be less than 0");

        this.balance = newBalance;
        this.getTransactions().add(transaction);
    }

    public MoneterayAmount getBalanceAmount() {
        return new MoneterayAmount(getBalance(), getCurrency());
    }
}
