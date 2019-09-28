package com.example.muchbetter;

import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.Assert.*;

public class TransactionTest {

    private static Validator validator;

    @BeforeClass
    public static void beforeClass() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void negativeAmountFailsValidation() {
        Transaction t1 = new Transaction(LocalDateTime.now(), "Item desc", new BigDecimal("-1"), "GBP");
        Set<ConstraintViolation<Transaction>> violations = validator.validate(t1);
        assertEquals(1, violations.size());
        assertEquals("amount", ((ConstraintViolation) violations.toArray()[0]).getPropertyPath().toString());
    }

    @Test
    public void futureDateFailsValidation() {
        Transaction t1 = new Transaction(LocalDateTime.now().plusDays(1), "Item desc", new BigDecimal("1"), "GBP");
        Set<ConstraintViolation<Transaction>> violations = validator.validate(t1);
        assertEquals(1, violations.size());
        assertEquals("date", ((ConstraintViolation) violations.toArray()[0]).getPropertyPath().toString());
    }

}