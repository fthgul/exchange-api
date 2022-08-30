package com.akinon.exchangeapi.constants;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    int getErrorCode();
    String getErrorKey();
    HttpStatus getHttpStatus();

}
