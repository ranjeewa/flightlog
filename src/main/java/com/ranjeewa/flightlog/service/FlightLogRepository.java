package com.ranjeewa.flightlog.service;

import com.ranjeewa.flightlog.domain.FlightLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightLogRepository extends ElasticsearchRepository<FlightLog, String> {

    FlightLog findByFlightLogFileName(String flightLogFileName);

}
