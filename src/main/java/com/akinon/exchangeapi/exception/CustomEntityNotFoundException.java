package com.akinon.exchangeapi.exception;

import lombok.Getter;

import javax.persistence.EntityNotFoundException;

@Getter
public class CustomEntityNotFoundException extends EntityNotFoundException implements ArgumentativeException {
    private final Object[] args;

    public CustomEntityNotFoundException(String message, Object... args) {
        super(message);
        this.args = args;
    }
}
