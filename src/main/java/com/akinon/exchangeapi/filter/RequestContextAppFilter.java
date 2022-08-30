package com.akinon.exchangeapi.filter;

import com.akinon.exchangeapi.model.filter.RequestContextApp;
import com.akinon.exchangeapi.util.RequestContextUtil;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Component
@Order(10)
public class RequestContextAppFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!this.isRequestValid(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String lang = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);
        Locale locale = lang != null ? new Locale(lang) : Locale.ENGLISH;
        final Optional<RequestContextApp> optionalRequestContext = RequestContextUtil.getRequestContextApp();
        RequestContextApp requestContextApp;
        if (optionalRequestContext.isEmpty()) {
            requestContextApp = RequestContextUtil.buildEmptyRequestContext();
        } else {
            requestContextApp = optionalRequestContext.get();
        }
        requestContextApp.setTransactionId(UUID.randomUUID().toString());
        requestContextApp.setLocale(locale);
        requestContextApp.setTransactionId(UUID.randomUUID().toString());
        requestContextApp.setPath(request.getRequestURI());
        filterChain.doFilter(request, response);
    }

    private boolean isRequestValid(HttpServletRequest request) {
        try {
            new URI(request.getRequestURL().toString());
            return true;
        } catch (URISyntaxException ex) {
            logger.error(ex);
            return false;
        }
    }
}
