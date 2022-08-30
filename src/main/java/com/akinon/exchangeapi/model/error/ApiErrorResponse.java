package com.akinon.exchangeapi.model.error;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ApiErrorResponse {
    private String status;
    private Date timestamp;
    private String path;
    private List<ApiErrorResponseItem> errors;

    public void addApiErrorResponseItem(ApiErrorResponseItem apiErrorResponseItem) {
        if(CollectionUtils.isEmpty(errors)) {
            errors = new ArrayList<>();
        }
        errors.add(apiErrorResponseItem);
    }
}
