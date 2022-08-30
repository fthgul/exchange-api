package com.akinon.exchangeapi.model.server.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;


@Getter
@Builder
public class ExchangeConversationResponse {
    private String transactionId;
    private String requestUrl;
    private String responseBody;
    private Date transactionTime;
}
