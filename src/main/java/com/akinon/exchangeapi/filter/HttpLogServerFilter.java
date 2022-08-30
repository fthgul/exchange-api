package com.akinon.exchangeapi.filter;

import com.akinon.exchangeapi.model.entity.HttpServerLog;
import com.akinon.exchangeapi.service.HttpServerLogService;
import com.akinon.exchangeapi.util.RequestContextUtil;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Iterator;

@Component
@Order(11)
public class HttpLogServerFilter extends OncePerRequestFilter {
    private final HttpServerLogService httpServerLogService;

    public HttpLogServerFilter(HttpServerLogService httpServerLogService) {
        this.httpServerLogService = httpServerLogService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!this.isRequestValid(httpServletRequest) ||
                !httpServletRequest.getRequestURI().startsWith("/exchange-api/convert")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            ContentCachingRequestWrapper requestWrapper =
                    new ContentCachingRequestWrapper(httpServletRequest);
            ContentCachingResponseWrapper responseWrapper =
                    new ContentCachingResponseWrapper(httpServletResponse);
            try {
                Date startTime = Date.from(Instant.now());
                filterChain.doFilter(requestWrapper, responseWrapper);
                Date endTime = Date.from(Instant.now());
                persistHttpServerLog(requestWrapper, responseWrapper, startTime, endTime);
            } finally {
                responseWrapper.copyBodyToResponse();
            }
        }
    }

    private void persistHttpServerLog(
            ContentCachingRequestWrapper requestWrapper,
            ContentCachingResponseWrapper responseWrapper,
            Date startTime,
            Date endTime) {
        try {
            HttpServerLog httpServerLog = new HttpServerLog();
            httpServerLog.setTransactionId(RequestContextUtil.getTransactionId());
            httpServerLog.setStartTime(startTime);
            httpServerLog.setEndTime(endTime);
            httpServerLog.setDuration(
                    startTime.toInstant().until(endTime.toInstant(), ChronoUnit.MILLIS));
            httpServerLog.setHttpMethod(requestWrapper.getMethod());
            httpServerLog.setResponseStatusCode(responseWrapper.getStatus());
            httpServerLog.setUrl(buildUrlWithRequestParams(requestWrapper));
            //todo currently requests are no request-body.So this implementation has not done yet.
            httpServerLog.setRequestBody("");
            httpServerLog.setResponseBody(new String(responseWrapper.getContentAsByteArray()));
            httpServerLogService.saveHttpServerLog(httpServerLog);
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    private String buildUrlWithRequestParams(ContentCachingRequestWrapper requestWrapper) {
        StringBuilder stringBuilder = new StringBuilder("?");
        final Iterator<String> paramIterator = requestWrapper.getParameterNames().asIterator();
        while (paramIterator.hasNext()) {
            final String param = paramIterator.next();
            final String paramValue = requestWrapper.getParameter(param);
            stringBuilder.append(param)
                    .append("=")
                    .append(paramValue);
            if (paramIterator.hasNext()) {
                stringBuilder.append("&");
            }
        }
        return requestWrapper.getRequestURL()
                .append(stringBuilder)
                .toString();
    }


        private boolean isRequestValid (HttpServletRequest request){
            try {
                new URI(request.getRequestURL().toString());
                return true;
            } catch (URISyntaxException ex) {
                logger.error(ex);
                return false;
            }
        }
    }
