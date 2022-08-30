package com.akinon.exchangeapi.exception;


import lombok.Getter;

@Getter
public class TransactionIdNotFoundException extends CustomEntityNotFoundException {
    public TransactionIdNotFoundException(String message, Object... args) {
        super(message, args);
    }
}
