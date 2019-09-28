package com.example.muchbetter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
class Transaction implements Serializable {

    @NonNull
    @PastOrPresent
    private LocalDateTime date;

    @NonNull
    private String description;

    @NonNull
    @PositiveOrZero
    private BigDecimal amount;

    @NonNull
    private String currency;

}