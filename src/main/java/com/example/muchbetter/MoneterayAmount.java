package com.example.muchbetter;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class MoneterayAmount {
    private BigDecimal amount;
    private String currency;
    public static final MoneterayAmount ZERO = new MoneterayAmount(BigDecimal.ZERO, "---");
}
