package com.akinon.exchangeapi.model.server.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Builder
public class GetExchangeRateListResponse {
    private Map<String, BigDecimal> exchangeRates;
}
