package com.akinon.exchangeapi.util;

import com.akinon.exchangeapi.model.filter.RequestContextApp;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Locale;
import java.util.Optional;

import static com.akinon.exchangeapi.model.filter.RequestContextApp.REQUEST_CONTEXT_APP_KEY;

public class RequestContextUtil {
    private RequestContextUtil() {
    }

    public static Optional<RequestContextApp> getRequestContextApp() {
        if (isExistRequestContext()) {
            final RequestContextApp requestContextApp = (RequestContextApp) RequestContextHolder.getRequestAttributes()
                    .getAttribute(REQUEST_CONTEXT_APP_KEY, 0);
            return Optional.ofNullable(requestContextApp);
        }
        return Optional.empty();
    }

    public static boolean isExistRequestContext() {
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return requestAttributes != null;
    }

    public static RequestContextApp buildEmptyRequestContext() {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        final RequestContextApp requestContextApp = new RequestContextApp();
        attributes.setAttribute(REQUEST_CONTEXT_APP_KEY, requestContextApp, 0);
        return requestContextApp;
    }

    public static String getTransactionId() {
        final Optional<RequestContextApp> requestContextApp = getRequestContextApp();
        if (requestContextApp.isPresent()) {
            return requestContextApp.get().getTransactionId();
        }
        return "";
    }

    public static Locale getRequestLocale() {
        final Optional<RequestContextApp> requestContextApp = getRequestContextApp();
        if (requestContextApp.isPresent()) {
            return requestContextApp.get().getLocale();
        }
        return Locale.ENGLISH;
    }
}
