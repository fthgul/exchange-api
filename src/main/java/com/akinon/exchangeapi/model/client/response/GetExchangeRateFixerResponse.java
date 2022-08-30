package com.akinon.exchangeapi.model.client.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class GetExchangeRateFixerResponse {
    private Boolean success;
    private long timestamp;
    private String date;
    private Map<String, BigDecimal> rates;
}
