package com.ranjeewa.flightlog.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "service", ignoreUnknownFields = false)
public class ServiceProperties {

    /**
     * Location for saving flight log files
     */
    private String filePath = "/Users/ranjeewa/Documents/";

    /**
     * Parameter name to read from time-series data
     *
     */
    private String paramName = "Aircraft Battery Voltage (mV)";

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }
}
