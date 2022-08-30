package com.akinon.exchangeapi.exceptionhandler;

import com.akinon.exchangeapi.constants.ErrorCodes;
import com.akinon.exchangeapi.exception.BusinessException;
import com.akinon.exchangeapi.exception.CustomEntityNotFoundException;
import com.akinon.exchangeapi.model.error.ApiErrorResponse;
import com.akinon.exchangeapi.model.error.ApiErrorResponseItem;
import com.akinon.exchangeapi.model.filter.RequestContextApp;
import com.akinon.exchangeapi.util.RequestContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class CommonExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiErrorResponse> handleUnknownException() {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(prepareUnknownErrorModel());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        ApiErrorResponse apiErrorResponse = prepareMethodArgumentValidationErrorModel(methodArgumentNotValidException);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(apiErrorResponse);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<ApiErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException servletRequestParameterException) {
        ApiErrorResponse apiErrorResponse = prepareMissingServletRequestParameterValidationErrorModel(servletRequestParameterException);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(apiErrorResponse);
    }

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<ApiErrorResponse> handleCommercePartnerBusinessException(BusinessException businessException) {
        return ResponseEntity
                .status(businessException.getHttpStatus())
                .body(prepareBusinessExceptionErrorModel(businessException));
    }


    @ExceptionHandler({CustomEntityNotFoundException.class})
    public ResponseEntity<?> handleCustomEntityNotFoundException(CustomEntityNotFoundException customEntityNotFoundException) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(prepareEntityNotFoundExceptionErrorModel(customEntityNotFoundException));
    }



    private ApiErrorResponse prepareEntityNotFoundExceptionErrorModel(CustomEntityNotFoundException customEntityNotFoundException) {
        final Optional<RequestContextApp> requestContextAppOptional = RequestContextUtil.getRequestContextApp();
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setStatus(HttpStatus.NOT_FOUND.getReasonPhrase());
        apiErrorResponse.setTimestamp(new Date());
        apiErrorResponse.setPath(requestContextAppOptional.isPresent()
                ? requestContextAppOptional.get().getPath()
                : "");
        apiErrorResponse.setErrors(Collections.singletonList(
                new ApiErrorResponseItem(
                        ErrorCodes.VALIDATION_REQUEST_BODY.getErrorCode(),
                        getSourceMessage(customEntityNotFoundException.getMessage(), customEntityNotFoundException.getArgs()))));
        return apiErrorResponse;
    }

    private ApiErrorResponse prepareBusinessExceptionErrorModel(BusinessException partnerBusinessException) {
        final Optional<RequestContextApp> requestContextAppOptional = RequestContextUtil.getRequestContextApp();
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setStatus(partnerBusinessException.getHttpStatus().getReasonPhrase());
        apiErrorResponse.setTimestamp(new Date());
        apiErrorResponse.setPath(requestContextAppOptional.isPresent()
                ? requestContextAppOptional.get().getPath()
                : "");
        apiErrorResponse.setErrors(Collections.singletonList(
                new ApiErrorResponseItem(partnerBusinessException.getCode(),
                        getSourceMessage(partnerBusinessException.getMessage(), partnerBusinessException.getArgs()))));
        return apiErrorResponse;
    }

    private ApiErrorResponse prepareMethodArgumentValidationErrorModel(MethodArgumentNotValidException methodArgumentNotValidException) {
        final Optional<RequestContextApp> requestContextAppOptional = RequestContextUtil.getRequestContextApp();
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setStatus(HttpStatus.BAD_REQUEST.getReasonPhrase());
        apiErrorResponse.setTimestamp(new Date());
        apiErrorResponse.setPath(requestContextAppOptional.isPresent()
                ? requestContextAppOptional.get().getPath()
                : "");
        for (ObjectError objectError : methodArgumentNotValidException.getBindingResult().getAllErrors()) {
            final String code = Arrays.stream(objectError.getCodes()).findFirst().get();
            apiErrorResponse.addApiErrorResponseItem(new ApiErrorResponseItem(
                            ErrorCodes.VALIDATION_REQUEST_BODY.getErrorCode(),
                            getSourceMessage(code, objectError.getArguments())
                    )
            );
        }
        return apiErrorResponse;
    }

    private ApiErrorResponse prepareMissingServletRequestParameterValidationErrorModel(MissingServletRequestParameterException servletRequestParameterException) {
        final Optional<RequestContextApp> requestContextAppOptional = RequestContextUtil.getRequestContextApp();
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setStatus(HttpStatus.BAD_REQUEST.getReasonPhrase());
        apiErrorResponse.setTimestamp(new Date());
        apiErrorResponse.setPath(requestContextAppOptional.isPresent()
                ? requestContextAppOptional.get().getPath()
                : "");
        apiErrorResponse.setErrors(Collections.singletonList(
                new ApiErrorResponseItem(
                        servletRequestParameterException.getMessage())));
        return apiErrorResponse;
    }

    private ApiErrorResponse prepareUnknownErrorModel() {
        final Optional<RequestContextApp> requestContextAppOptional = RequestContextUtil.getRequestContextApp();
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        apiErrorResponse.setTimestamp(new Date());
        apiErrorResponse.setPath(requestContextAppOptional.isPresent()
                ? requestContextAppOptional.get().getPath()
                : "");
        apiErrorResponse.setErrors(Collections.singletonList(
                new ApiErrorResponseItem(ErrorCodes.UNKNOWN.getErrorCode(),
                        getSourceMessage(ErrorCodes.UNKNOWN.getErrorKey(), null))));
        return apiErrorResponse;
    }

    private String getSourceMessage(String code, Object[] args) {
        String userMessage;
        try {
            userMessage = messageSource.getMessage(code, args, RequestContextUtil.getRequestLocale());
        } catch (NoSuchMessageException noSuchMessageException) {
            log.error("An error occurred while getting message code: {}", code, noSuchMessageException);
            userMessage = messageSource.getMessage(ErrorCodes.UNKNOWN.getErrorKey(),
                    null,
                    RequestContextUtil.getRequestLocale());
        }
        return userMessage;
    }
}
