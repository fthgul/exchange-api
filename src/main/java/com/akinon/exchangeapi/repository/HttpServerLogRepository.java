package com.akinon.exchangeapi.repository;

import com.akinon.exchangeapi.model.entity.HttpServerLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HttpServerLogRepository  extends CrudRepository<HttpServerLog, String> {
    List<HttpServerLog> findHttpServerLogsByStartTimeAfterAndEndTimeBefore(Date startDate, Date endDate);
}
