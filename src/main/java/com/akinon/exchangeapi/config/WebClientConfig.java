package com.akinon.exchangeapi.config;

import io.netty.handler.logging.LogLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;



@Configuration
public class WebClientConfig {
    @Value("${api.fixer.baseUrl}")
    private String baseUrl;

    @Value("${api.fixer.apiKey}")
    private String secretKey;

    @Bean(name = "webClientFixer")
    public WebClient webClientFixer() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(buildHttpClient()))
                .baseUrl(baseUrl)
                .defaultHeader("apiKey", secretKey)
                .build();
    }

    private HttpClient buildHttpClient() {
        return HttpClient
                .create()
                .wiretap("reactor.netty.http.client.HttpClient",
                        LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);
    }
}
