package com.akinon.exchangeapi.model.filter;

import lombok.*;

import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestContextApp {
    public static final String REQUEST_CONTEXT_APP_KEY = "requestContextApp";
    private Locale locale;
    private String path;
    private String transactionId;
}
