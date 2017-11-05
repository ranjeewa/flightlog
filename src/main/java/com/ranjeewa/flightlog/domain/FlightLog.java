package com.ranjeewa.flightlog.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "flightlogs", type = "flightlog", shards = 1, replicas = 0, refreshInterval = "-1")
public class FlightLog {

    @Id
    private String id;

    private String flightLogFileName;

    private String startValue;

    private String endValue;

    public FlightLog(String flightLogFileName, String startValue, String endValue) {
        this.flightLogFileName = flightLogFileName;
        this.startValue = startValue;
        this.endValue = endValue;
    }

    public FlightLog() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlightLogFileName() {
        return flightLogFileName;
    }

    public void setFlightLogFileName(String flightLogFileName) {
        this.flightLogFileName = flightLogFileName;
    }

    public String getStartValue() {
        return startValue;
    }

    public void setStartValue(String startValue) {
        this.startValue = startValue;
    }

    public String getEndValue() {
        return endValue;
    }

    public void setEndValue(String endValue) {
        this.endValue = endValue;
    }
}
