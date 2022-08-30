package com.akinon.exchangeapi.model.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class HttpServerLog {
    @Id
    @Column(name = "transaction_id", nullable = false)
    private String transactionId;
    private Date startTime;
    private Date endTime;
    private Long duration;
    private String url;
    private String httpMethod;
    private String requestBody;
    private String requestParams;
    @Column(columnDefinition="TEXT")
    private String responseBody;
    private int responseStatusCode;
}
