package com.akinon.exchangeapi.model.error;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiErrorResponseItem {
    private Integer errorCode;
    private String message;

    public ApiErrorResponseItem(String message) {
        this.message = message;
    }

    public ApiErrorResponseItem(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
