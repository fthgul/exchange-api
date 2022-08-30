package com.akinon.exchangeapi.model.server.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;

@Builder
@Getter
public class ConvertExchangeResponse {
    private String transactionId;
    private Map<String, BigDecimal> calculatedExchanges;
}
