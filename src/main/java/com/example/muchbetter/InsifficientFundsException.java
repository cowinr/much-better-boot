package com.example.muchbetter;

public class InsifficientFundsException extends RuntimeException {

    public InsifficientFundsException(String message) {
        super(message);
    }

}
