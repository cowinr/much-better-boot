package com.example.muchbetter;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void applyTransactionHappyPath() {
        User u1 = new User("id", BigDecimal.TEN, "GBP");
        Transaction t1 = new Transaction(LocalDateTime.now(), "Item desc", new BigDecimal("1"), "GBP");
        u1.applyTransaction(t1);
        assertEquals(1, u1.getTransactions().size());
        assertEquals(new BigDecimal("9"), u1.getBalance());
    }

    @Test(expected = IllegalArgumentException.class)
    public void applyTransactionOfWrongCurrencyThrowsIllegalArgumentException() {
        User u1 = new User("id", BigDecimal.TEN, "GBP");
        Transaction t1 = new Transaction(LocalDateTime.now(), "Item desc", new BigDecimal("1"), "USD");
        u1.applyTransaction(t1);
    }

    @Test(expected = InsifficientFundsException.class)
    public void applyTooLargeTransactionThrowsInsifficientFundsException() {
        User u1 = new User("id", BigDecimal.TEN, "GBP");
        Transaction t1 = new Transaction(LocalDateTime.now(), "Item desc", new BigDecimal("100"), "GBP");
        u1.applyTransaction(t1);
    }
}