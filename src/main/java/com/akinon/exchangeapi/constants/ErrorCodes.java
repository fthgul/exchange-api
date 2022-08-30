package com.akinon.exchangeapi.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Locale;

@Getter
public enum ErrorCodes implements ErrorCode {

    //entity_not_found_exceptions
    TRANSACTION_ID_NOT_FOUND(100, HttpStatus.NOT_FOUND),
    VALIDATION_REQUEST_BODY(101, HttpStatus.BAD_REQUEST),
    START_DATE_AND_END_DATE_MUST_NOT_NULL(104, HttpStatus.BAD_REQUEST),
    START_DATE_MUST_NOT_AFTER_END_DATE(105, HttpStatus.BAD_REQUEST),

    INVALID_SOURCE_CURRENCY(102, HttpStatus.BAD_REQUEST),
    INVALID_TARGET_CURRENCY(103, HttpStatus.BAD_REQUEST),


    UNKNOWN(900, HttpStatus.INTERNAL_SERVER_ERROR);
    private final int errorCode;
    private final HttpStatus httpStatus;

    ErrorCodes(int errorCode, HttpStatus httpStatus) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }


    @Override
    public String getErrorKey() {
        return "exception." + this.name().toLowerCase(Locale.ROOT);
    }
}
