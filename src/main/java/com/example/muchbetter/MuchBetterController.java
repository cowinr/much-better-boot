package com.example.muchbetter;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@Api("MuchBetter")
public class MuchBetterController {

    private TokenGenerator tokenGenerator;

    private UserRepository repository;

    public MuchBetterController(TokenGenerator tokenGenerator, UserRepository repository) {
        this.tokenGenerator = tokenGenerator;
        this.repository = repository;
    }

    /**
     * Every call to /login will return a new token and every invocation to this endpoint creates a new user,
     * gives them a preset balance in a preset currency.
     *
     * @return a token (which need to be used in subsequent calls to the API, in the Authorization header).
     */
    @ApiOperation("Login")
    @PostMapping("/login")
    public ResponseEntity<String> login() {
        String token = tokenGenerator.generateToken();

        repository.save(new User(token, new BigDecimal("100"), "GBP"));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Authorization", token)
                .build();
    }

    /**
     * @return the current balance along with the currency code.
     */
    @ApiOperation("Retrieve user's balance")
    @GetMapping("/balance")
    public MoneterayAmount balance(@RequestHeader("Authorization") String authorization) {
        Optional<User> user = authenticate(authorization);
        return user
                .map(User::getBalanceAmount)
                .orElseGet(() -> MoneterayAmount.ZERO);
    }

    /**
     * @return a list of transactions done by the user with the date, description, amount,
     * currency for each transaction.
     */
    @ApiOperation("Retrieve user's list of transactions")
    @GetMapping("/transactions")
    public List<Transaction> transactions(@RequestHeader("Authorization") String authorization) {
        Optional<User> user = authenticate(authorization);
        return user.map(User::getTransactions).orElse(new ArrayList<>());
    }

    /**
     * Register the user spending something.
     * <p>
     * TODO Move to service
     */
    @ApiOperation("Apply transaction to user's list of transaction")
    @PostMapping("/spend")
    public ResponseEntity<String> spend(@RequestBody final Transaction transaction, @RequestHeader("Authorization") String authorization) {

        Optional<User> user = authenticate(authorization);

        if (user.isPresent()) {
            // Apply transaction
            user.get().applyTransaction(transaction);
            repository.save(user.get());
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .build();
        }
        // Effectively the user is not presenting valid credentials
        else {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }
    }

    /**
     * Find user by auth token. This is like login plus user lookup.
     *
     * @param authorization The Authorization token passed as a request header.
     * @return The details for the user associated with the auth token.
     */
    private Optional<User> authenticate(String authorization) {
        return repository.findById(authorization.substring(7));
    }

}
