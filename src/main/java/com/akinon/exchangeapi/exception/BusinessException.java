package com.akinon.exchangeapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException implements ArgumentativeException {
    private static final long serialVersionUID = 1L;

    private int code;
    private HttpStatus httpStatus;
    private Object[] args;

    public BusinessException(String message, Throwable t) {
        super(message, t);
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Throwable t) {
        super(t);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(int code, HttpStatus httpStatus, String message, Object... args) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
        this.args = args;
    }
}
