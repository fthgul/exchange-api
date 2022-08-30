package com.akinon.exchangeapi.repository;

import com.akinon.exchangeapi.model.entity.HttpServerLog;
import com.akinon.exchangeapi.model.server.response.ConvertExchangeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@DataJpaTest
class HttpServerLogRepositoryTest {

    @Autowired
    HttpServerLogRepository httpServerLogRepository;

    @DisplayName("todo")
    @Test
    void givenStartDateAndEnd_whenQueryByStartDateAndEndDate_ThenReturnOneHttpServerLogList() {
        for (int i = 0; i < 2; i++) {
            httpServerLogRepository.save(buildHttpServerLogEntity());
        }

        final List<HttpServerLog> httpServerLogEntityList = httpServerLogRepository
                .findHttpServerLogsByStartTimeAfterAndEndTimeBefore(
                        Date.valueOf(LocalDate.now().minusDays(1)),
                        Date.valueOf(LocalDate.now().plusDays(1)));
        assertEquals(2, httpServerLogEntityList.size());
    }

    @Test
    void givenStartDateAndEnd_whenQueryByStartDateAndEndDate_ThenReturnEmptyList() {
        for (int i = 0; i < 2; i++) {
            httpServerLogRepository.save(buildHttpServerLogEntity());
        }

        final List<HttpServerLog> httpServerLogEntityList = httpServerLogRepository
                .findHttpServerLogsByStartTimeAfterAndEndTimeBefore(
                        Date.valueOf(LocalDate.now().plusDays(1)),
                        Date.valueOf(LocalDate.now().plusDays(2)));
        assertEquals(0, httpServerLogEntityList.size());
    }

    @Test
    void givenHttpServerLog_whenSaveHttpServerLog_thenReturnHttpServerLog() {
        HttpServerLog httpServerLog = buildHttpServerLogEntity();
        httpServerLogRepository.save(httpServerLog);
        final HttpServerLog httpServerLogEntity = httpServerLogRepository.findById(httpServerLog.getTransactionId())
                .orElse(null);
        assertNotNull(httpServerLogEntity);
    }


    private HttpServerLog buildHttpServerLogEntity() {
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
            return httpServerLog;
        } catch (Exception e) {
            log.error("An error occurred:", e);
        }
        return null;
    }
}
