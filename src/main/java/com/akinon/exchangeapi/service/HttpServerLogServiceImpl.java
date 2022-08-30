package com.akinon.exchangeapi.service;

import com.akinon.exchangeapi.model.entity.HttpServerLog;
import com.akinon.exchangeapi.repository.HttpServerLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class HttpServerLogServiceImpl implements HttpServerLogService {
    private final HttpServerLogRepository httpServerLogRepository;

    @Override
    public HttpServerLog saveHttpServerLog(HttpServerLog httpServerLog) {
        return httpServerLogRepository.save(httpServerLog);
    }
}
