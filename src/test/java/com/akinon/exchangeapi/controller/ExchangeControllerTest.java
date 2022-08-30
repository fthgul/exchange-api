package com.akinon.exchangeapi.controller;

import com.akinon.exchangeapi.model.server.response.ConvertExchangeResponse;
import com.akinon.exchangeapi.model.server.response.GetExchangeRateListResponse;
import com.akinon.exchangeapi.service.ExchangeService;
import com.akinon.exchangeapi.service.HttpServerLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ExchangeController.class)
class ExchangeControllerTest {
    @MockBean
    private ExchangeService exchangeService;

    @MockBean
    private HttpServerLogService httpServerLogService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void givenValidExchangeRequest_whenQueryExchangeRates_thenReturnSuccessfulResponse() throws Exception {
        when(exchangeService.getExchangeRates("USD", Collections.singletonList("TRY")))
                .thenReturn(buildExchangeResponse());
        mockMvc.perform(
                        get("/exchange-api/exchange-rates")
                                .param("sourceCurrency", "USD")
                                .param("targetCurrencyList", "TRY")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void givenInCompleteExchangeRequest_whenQueryExchangeRates_thenThrowException() throws Exception {
        mockMvc.perform(
                        get("/exchange-api/exchange-rates")
                                .param("targetCurrencyList", "TRY")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void givenValidCalculateExchangeRequest_whenQueryExchangeRates_thenReturn() throws Exception {
        when(exchangeService.calculateExchangeRates("USD", Collections.singletonList("TRY"), new BigDecimal(10)))
                .thenReturn(buildCalculateExchangeResponse());
        mockMvc.perform(
                        get("/exchange-api/convert")
                                .param("sourceCurrency", "USD")
                                .param("targetCurrencyList", "TRY")
                                .param("amount", "10")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void givenInCompleteCalculateExchangeRequest_whenQueryExchangeRates_thenThrowException() throws Exception {
        mockMvc.perform(
                        get("/exchange-api/convert")
                                .param("targetCurrencyList", "TRY")
                                .param("amount", "10")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void givenConversationRequest_whenQueryConversation_thenReturn() throws Exception {
        when(exchangeService.getConversationsByStartAndEndTime(any(), any()))
                .thenReturn(any());
        mockMvc.perform(
                        get("/exchange-api/conversations")
                                .param("transactionId", UUID.randomUUID().toString())
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidDateConversationRequest_whenQueryConversation_thenThrowException() throws Exception {
        mockMvc.perform(
                        get("/exchange-api/conversations")
                                .param("fromDate", "2022-07-29")
                                .param("toDate", "2022-07-28")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    private ConvertExchangeResponse buildCalculateExchangeResponse() {
        Map<String, BigDecimal> exchangeResponseMap  = new HashMap<>() {{
            put("TRY", new BigDecimal(200));
        }};
        return ConvertExchangeResponse.builder()
                .calculatedExchanges(exchangeResponseMap)
                .transactionId(generateTransactionId())
                .build();
    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }

    private GetExchangeRateListResponse buildExchangeResponse() {
        Map<String, BigDecimal> exchangeResponseMap  = new HashMap<>() {{
            put("TRY", new BigDecimal(20));
        }};
        return GetExchangeRateListResponse.builder()
                .exchangeRates(exchangeResponseMap)
                .build();
    }
}
