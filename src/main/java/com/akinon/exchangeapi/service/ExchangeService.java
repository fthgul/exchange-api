package com.akinon.exchangeapi.service;

import com.akinon.exchangeapi.model.server.response.ConvertExchangeResponse;
import com.akinon.exchangeapi.model.server.response.ExchangeConversationResponse;
import com.akinon.exchangeapi.model.server.response.GetExchangeRateListResponse;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface ExchangeService {
    GetExchangeRateListResponse getExchangeRates(String sourceCurrency, List<String> targetCurrency);

    ConvertExchangeResponse calculateExchangeRates(String sourceCurrency, List<String> targetCurrency, BigDecimal amount);

    ExchangeConversationResponse getConversationsByTransactionId(String transactionId);

    List<ExchangeConversationResponse> getConversationsByStartAndEndTime(Date startDate, Date endDate);
}
