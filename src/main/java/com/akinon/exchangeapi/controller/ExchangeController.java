package com.akinon.exchangeapi.controller;

import com.akinon.exchangeapi.constants.ErrorCodes;
import com.akinon.exchangeapi.exception.BusinessException;
import com.akinon.exchangeapi.model.server.response.ConvertExchangeResponse;
import com.akinon.exchangeapi.model.server.response.ExchangeConversationResponse;
import com.akinon.exchangeapi.model.server.response.GetExchangeRateListResponse;
import com.akinon.exchangeapi.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("exchange-api")
public class ExchangeController {

    private final ExchangeService exchangeService;

    @GetMapping("/exchange-rates")
    public GetExchangeRateListResponse getExchangeRates(@RequestParam @NotBlank String sourceCurrency,
                                                        @RequestParam @Size(min = 1) List<String> targetCurrencyList) {
        return exchangeService.getExchangeRates(sourceCurrency, targetCurrencyList);
    }

    @GetMapping("/convert")
    public ConvertExchangeResponse convertExchangeRates(@RequestParam @NotBlank String sourceCurrency,
                                                        @RequestParam @Size(min = 1) List<String> targetCurrencyList,
                                                        @RequestParam @NotNull BigDecimal amount) {
        return exchangeService.calculateExchangeRates(sourceCurrency, targetCurrencyList, amount);
    }

    @GetMapping("/conversations")
    public List<ExchangeConversationResponse> getExchangeConversations(
            @RequestParam(name = "transactionId", required = false) String transactionId,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate) {

        if(StringUtils.hasText(transactionId)) {
            return Collections.singletonList(exchangeService.getConversationsByTransactionId(transactionId));
        } else {
            validateDates(fromDate, toDate);
            return exchangeService.getConversationsByStartAndEndTime(fromDate, toDate);
        }
    }

    private void validateDates(Date startDate, Date endDate) {
        if (Objects.isNull(startDate) || Objects.isNull(endDate)) {
            throw new BusinessException(
                    ErrorCodes.START_DATE_AND_END_DATE_MUST_NOT_NULL.getErrorCode(),
                    HttpStatus.BAD_REQUEST,
                    ErrorCodes.START_DATE_AND_END_DATE_MUST_NOT_NULL.getErrorKey());
        }

        if(startDate.after(endDate)) {
            throw new BusinessException(
                    ErrorCodes.START_DATE_MUST_NOT_AFTER_END_DATE.getErrorCode(),
                    HttpStatus.BAD_REQUEST,
                    ErrorCodes.START_DATE_MUST_NOT_AFTER_END_DATE.getErrorKey());
        }
    }
}
