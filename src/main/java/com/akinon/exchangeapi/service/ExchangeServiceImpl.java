package com.akinon.exchangeapi.service;

import com.akinon.exchangeapi.client.FixerWebClient;
import com.akinon.exchangeapi.constants.CurrencyStaticCodes;
import com.akinon.exchangeapi.constants.ErrorCodes;
import com.akinon.exchangeapi.exception.BusinessException;
import com.akinon.exchangeapi.exception.TransactionIdNotFoundException;
import com.akinon.exchangeapi.model.client.response.GetExchangeRateFixerResponse;
import com.akinon.exchangeapi.model.entity.HttpServerLog;
import com.akinon.exchangeapi.model.server.response.ConvertExchangeResponse;
import com.akinon.exchangeapi.model.server.response.ExchangeConversationResponse;
import com.akinon.exchangeapi.model.server.response.GetExchangeRateListResponse;
import com.akinon.exchangeapi.repository.HttpServerLogRepository;
import com.akinon.exchangeapi.util.RequestContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExchangeServiceImpl implements ExchangeService {

    private final FixerWebClient fixerWebClient;
    private final HttpServerLogRepository httpServerLogRepository;

    @Override
    public GetExchangeRateListResponse getExchangeRates(String sourceCurrency, List<String> targetCurrencyList) {
        validateExchangeRequest(sourceCurrency, targetCurrencyList);
        final GetExchangeRateFixerResponse exchangeRateFixerResponse = fixerWebClient
                .getExchangeRateFixerResponse(sourceCurrency, targetCurrencyList);
        return GetExchangeRateListResponse.builder()
                .exchangeRates(exchangeRateFixerResponse.getRates())
                .build();
    }

    @Override
    public ConvertExchangeResponse calculateExchangeRates(String sourceCurrency,
                                                          List<String> targetCurrencyList,
                                                          BigDecimal amount) {
        validateExchangeRequest(sourceCurrency, targetCurrencyList);
        final GetExchangeRateFixerResponse exchangeRateFixerResponse = fixerWebClient
                .getExchangeRateFixerResponse(sourceCurrency, targetCurrencyList);
        if (isNotSuccessClientResponse(exchangeRateFixerResponse)) {
            return buildEmptyConvertExchangeResponse();
        } else {
            return buildSuccessConvertExchangeResponse(exchangeRateFixerResponse, amount);
        }
    }

    @Override
    public ExchangeConversationResponse getConversationsByTransactionId(String transactionId) {
        final HttpServerLog httpServerLog = httpServerLogRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionIdNotFoundException(
                        ErrorCodes.TRANSACTION_ID_NOT_FOUND.getErrorKey(),
                        transactionId
                ));
        return ExchangeConversationResponse.builder()
                .requestUrl(httpServerLog.getUrl())
                .responseBody(httpServerLog.getResponseBody())
                .transactionId(httpServerLog.getTransactionId())
                .transactionTime(httpServerLog.getStartTime())
                .build();
    }

    @Override
    public List<ExchangeConversationResponse> getConversationsByStartAndEndTime(Date startDate, Date endDate) {
        final List<HttpServerLog> httpServerLogs = httpServerLogRepository
                .findHttpServerLogsByStartTimeAfterAndEndTimeBefore(startDate, endDate);
        return httpServerLogs.stream()
                .map(httpServerLog ->
                        ExchangeConversationResponse.builder()
                                .requestUrl(httpServerLog.getUrl())
                                .responseBody(httpServerLog.getResponseBody())
                                .transactionId(httpServerLog.getTransactionId())
                                .transactionTime(httpServerLog.getStartTime())
                                .build()
                ).collect(Collectors.toList());
    }

    private ConvertExchangeResponse buildSuccessConvertExchangeResponse(
            GetExchangeRateFixerResponse exchangeRateFixerResponse,
            BigDecimal amount) {
        Map<String, BigDecimal> calculatedExchangeRateAmounts = calculateAmountsForEachTargetCurrency(
                exchangeRateFixerResponse.getRates(),
                amount
        );
        return ConvertExchangeResponse.builder()
                .transactionId(RequestContextUtil.getTransactionId())
                .calculatedExchanges(calculatedExchangeRateAmounts)
                .build();
    }

    private ConvertExchangeResponse buildEmptyConvertExchangeResponse() {
        return ConvertExchangeResponse.builder()
                .transactionId(RequestContextUtil.getTransactionId())
                .calculatedExchanges(null)
                .build();
    }

    private boolean isNotSuccessClientResponse(GetExchangeRateFixerResponse exchangeRateFixerResponse) {
        if (!exchangeRateFixerResponse.getSuccess()) {
            log.error("exchange client api has occurred an error");
            return true;
        }
        return false;
    }

    private Map<String, BigDecimal> calculateAmountsForEachTargetCurrency(Map<String, BigDecimal> rateExchangeMap, BigDecimal amount) {
        Map<String, BigDecimal> calculatedExchangeRateMaps = new HashMap<>();
        for (Map.Entry<String, BigDecimal> entrySetExchangeRates : rateExchangeMap.entrySet()) {
            calculatedExchangeRateMaps.put(
                    entrySetExchangeRates.getKey(),
                    entrySetExchangeRates.getValue().multiply(amount)
            );
        }
        return calculatedExchangeRateMaps;
    }

    private void validateExchangeRequest(String sourceCurrency, List<String> targetCurrencyList) {
        if (!CurrencyStaticCodes.currencyCodes.contains(sourceCurrency)) {
            throw new BusinessException(ErrorCodes.INVALID_SOURCE_CURRENCY.getErrorCode(),
                    HttpStatus.BAD_REQUEST,
                    ErrorCodes.INVALID_SOURCE_CURRENCY.getErrorKey(),
                    sourceCurrency);
        }
        for (String targetCurrency : targetCurrencyList) {
            if (!CurrencyStaticCodes.currencyCodes.contains(targetCurrency)) {
                throw new BusinessException(ErrorCodes.INVALID_TARGET_CURRENCY.getErrorCode(),
                        HttpStatus.BAD_REQUEST,
                        ErrorCodes.INVALID_TARGET_CURRENCY.getErrorKey(),
                        targetCurrency);
            }
        }
    }
}
