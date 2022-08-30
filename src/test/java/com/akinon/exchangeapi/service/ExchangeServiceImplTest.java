package com.akinon.exchangeapi.service;

import com.akinon.exchangeapi.client.FixerWebClient;
import com.akinon.exchangeapi.constants.ErrorCodes;
import com.akinon.exchangeapi.exception.BusinessException;
import com.akinon.exchangeapi.exception.TransactionIdNotFoundException;
import com.akinon.exchangeapi.model.client.response.GetExchangeRateFixerResponse;
import com.akinon.exchangeapi.model.entity.HttpServerLog;
import com.akinon.exchangeapi.model.server.response.ConvertExchangeResponse;
import com.akinon.exchangeapi.model.server.response.ExchangeConversationResponse;
import com.akinon.exchangeapi.model.server.response.GetExchangeRateListResponse;
import com.akinon.exchangeapi.repository.HttpServerLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@Slf4j
@SpringBootTest(classes = ExchangeServiceImpl.class)
class ExchangeServiceImplTest {
    @Autowired
    ExchangeService exchangeService;

    @MockBean
    FixerWebClient fixerWebClient;

    @MockBean
    HttpServerLogRepository httpServerLogRepository;

    @Test
    void givenValidExchangeRateSourceCurrency_whenCallExchangeApi_ThenReturnSuccessfulResponse() {
        final GetExchangeRateFixerResponse exchangeRateFixerResponse = buildSuccessExchangeFixerResponse();
        Mockito.when(fixerWebClient.getExchangeRateFixerResponse(any(),any()))
                .thenReturn(exchangeRateFixerResponse);
        final GetExchangeRateListResponse exchangeRates = exchangeService.getExchangeRates("USD",
                Collections.singletonList("TRY"));
        assertEquals(1, exchangeRates.getExchangeRates().size());
    }

    @Test
    void givenInValidExchangeRateSourceCurrency_whenCallExchangeApi_ThenThrowBusinessException() {
        assertThrows(BusinessException.class, () ->
                exchangeService.getExchangeRates("USR", Collections.singletonList("TRY")));
    }

    @Test
    void givenInValidExchangeRateTargetCurrency_whenCallExchangeApi_ThenThrowBusinessException() {
        assertThrows(BusinessException.class, () ->
                exchangeService.getExchangeRates("USD", Collections.singletonList("TRQ")));
    }

    @Test
    void givenExchangeRateAmount_whenCallExchangeApi_ThenReturnSuccessfulResponse() {
        final GetExchangeRateFixerResponse exchangeRateFixerResponse = buildSuccessExchangeFixerResponse();
        Mockito.when(fixerWebClient.getExchangeRateFixerResponse(any(),any()))
                .thenReturn(exchangeRateFixerResponse);
        final ConvertExchangeResponse convertExchangeResponse =
                exchangeService.calculateExchangeRates("USD", Collections.singletonList("TRY"), new BigDecimal(10));
        for (Map.Entry<String, BigDecimal> calculatedEntrySet : convertExchangeResponse.getCalculatedExchanges().entrySet()) {
            assertEquals("TRY", calculatedEntrySet.getKey());
            assertEquals(new BigDecimal(200), calculatedEntrySet.getValue());
        }
    }

    @Test
    void givenStartDateAndEndDate_whenQueryConversation_ThenReturnConversations() {
        Mockito.when(httpServerLogRepository.findHttpServerLogsByStartTimeAfterAndEndTimeBefore(any(),any()))
                .thenReturn(Collections.singletonList(new HttpServerLog()));
        final List<ExchangeConversationResponse> conversationResponseList = exchangeService.getConversationsByStartAndEndTime(Date.valueOf(LocalDate.MIN),
                Date.valueOf(LocalDate.MAX));
        assertEquals(1, conversationResponseList.size());
    }


    @Test
    void givenStartDateAndEndDate_whenQueryConversation_ThenReturnEmptyListOfConversations() {
        Mockito.when(httpServerLogRepository.findHttpServerLogsByStartTimeAfterAndEndTimeBefore(any(),any()))
                .thenReturn(Collections.emptyList());
        final List<ExchangeConversationResponse> conversationResponseList = exchangeService.getConversationsByStartAndEndTime(Date.valueOf(LocalDate.MIN),
                Date.valueOf(LocalDate.MAX));
        assertEquals(0, conversationResponseList.size());
    }

    @Test
    void givenValidTransactionId_whenQueryConversation_ThenReturnConversation() {
        Mockito.when(httpServerLogRepository.findById(any()))
                .thenReturn(buildHttpServerLogEntity());
        final ExchangeConversationResponse conversationResponse =
                exchangeService.getConversationsByTransactionId(UUID.randomUUID().toString());
        assertNotNull(conversationResponse);
    }

    @Test
    void givenInValidTransactionId_whenQueryConversation_ThenThrowBusinessException() {
        Mockito.when(httpServerLogRepository.findById(any()))
                .thenThrow(new TransactionIdNotFoundException(ErrorCodes.TRANSACTION_ID_NOT_FOUND.getErrorKey(),
                        generateTransactionId()));
        assertThrows(TransactionIdNotFoundException.class, () ->
                exchangeService.getConversationsByTransactionId(generateTransactionId()));
    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }


    private GetExchangeRateFixerResponse buildSuccessExchangeFixerResponse() {
        Map<String, BigDecimal> exchangeResponseMap  = new HashMap<>() {{
            put("TRY", new BigDecimal(20));
        }};
        GetExchangeRateFixerResponse exchangeRateFixerResponse = new GetExchangeRateFixerResponse();
        exchangeRateFixerResponse.setSuccess(true);
        exchangeRateFixerResponse.setDate("2022-10-10");
        exchangeRateFixerResponse.setRates(exchangeResponseMap);
        exchangeRateFixerResponse.setTimestamp(1661759978L);
        return exchangeRateFixerResponse;
    }

    private Optional<HttpServerLog> buildHttpServerLogEntity() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            HttpServerLog httpServerLog = new HttpServerLog();
            String transactionId = UUID.randomUUID().toString();
            httpServerLog.setTransactionId(UUID.randomUUID().toString());
            httpServerLog.setStartTime(new java.util.Date());
            httpServerLog.setEndTime(new java.util.Date());
            httpServerLog.setHttpMethod("GET");
            httpServerLog.setRequestBody("");
            httpServerLog.setResponseBody(mapper.writeValueAsString(ConvertExchangeResponse.builder()
                    .transactionId(transactionId)
                    .calculatedExchanges(Collections.singletonMap("USD", new BigDecimal("18.30")))
                    .build()));
            return Optional.of(httpServerLog);
        } catch (Exception e) {
            log.error("An error occurred:", e);
        }
        return Optional.empty();
    }
}
