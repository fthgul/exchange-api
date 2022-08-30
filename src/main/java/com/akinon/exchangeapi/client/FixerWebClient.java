package com.akinon.exchangeapi.client;

import com.akinon.exchangeapi.model.client.response.GetExchangeRateFixerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FixerWebClient {
    private final WebClient webClientFixer;

    public GetExchangeRateFixerResponse getExchangeRateFixerResponse(String sourceCurrency, List<String> targetCurrency) {
        return webClientFixer.get()
                .uri(uriBuilder -> uriBuilder.path("/latest")
                        .queryParam("base", sourceCurrency)
                        .queryParam("symbols", buildFormatSymbols(targetCurrency))
                        .build())
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, ClientResponse::createException)
                .bodyToMono(GetExchangeRateFixerResponse.class)
                .defaultIfEmpty(buildEmptyGetExchangeFixerResponse())
                .block();
    }

    private String buildFormatSymbols(List<String> targetCurrency) {
        StringBuilder stringBuilder = new StringBuilder();
        targetCurrency.forEach(currency -> stringBuilder.append(currency).append(","));
        return  stringBuilder.toString();
    }

    private GetExchangeRateFixerResponse buildEmptyGetExchangeFixerResponse() {
        GetExchangeRateFixerResponse response = new GetExchangeRateFixerResponse();
        response.setSuccess(false);
        response.setTimestamp(Timestamp.from(Instant.now()).getTime());
        return response;
    }
}
