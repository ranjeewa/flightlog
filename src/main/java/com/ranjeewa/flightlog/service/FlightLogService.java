package com.ranjeewa.flightlog.service;

import com.ranjeewa.flightlog.domain.FlightLog;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class FlightLogService {

    private final ServiceProperties serviceProperties;
    private final FlightLogRepository flightLogRepository;
    private final FileService fileService;
    private final FlightLogParser flightLogParser;

    public FlightLogService(ServiceProperties serviceProperties, FlightLogRepository flightLogRepository,
                            FileService fileService, FlightLogParser flightLogParser) {
        this.serviceProperties = serviceProperties;
        this.flightLogRepository = flightLogRepository;
        this.fileService = fileService;
        this.flightLogParser = flightLogParser;
    }

    public void saveFlightLogValues(String fileName) {

        File file = fileService.findFile(fileName);

        if (file != null && !file.exists()) {
            return;
        }

        String valueName = serviceProperties.getParamName();
        FlightLog flightLog = flightLogParser.findFirstAndLastValuesFromFile(file, valueName);
        if (flightLog != null) {
            flightLogRepository.save(flightLog);
        }
    }




}
