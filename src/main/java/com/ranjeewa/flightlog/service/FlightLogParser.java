package com.ranjeewa.flightlog.service;

import com.ranjeewa.flightlog.domain.FlightLog;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;

@Component
@Description("Helper class to parse flight log file. Contains a method to find first & last values of a given column")
public class FlightLogParser {

    Logger logger = LoggerFactory.getLogger(FlightLogParser.class);

    FlightLog findFirstAndLastValuesFromFile(File file, String valueName) {

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            boolean readingTSData = false;
            int columnPosition = -1;
            String firstValue = null, lastValue = null;

            String line = br.readLine();
            while (line != null) {
                if (!readingTSData) { //still in header section
                    if (line.contains(valueName)) {
                        //Header line for time-series data
                        String[] headers = line.split("\\t");
                        List<String> headerList = Arrays.asList(headers);
                        columnPosition = headerList.indexOf(valueName);
                        readingTSData = true;
                    }
                } else {
                    if (line.startsWith("#")) { //end of time-series data
                        break;
                    }
                    String[] values = line.split("\\t");
                    String thisValue = values[columnPosition];
                    if (firstValue == null) {
                        firstValue = thisValue;
                    }
                    lastValue = thisValue;
                }
                line = br.readLine();
            }
            br.close();
            return new FlightLog(file.getName(), firstValue, lastValue);

        } catch (Exception e) {
            logger.error("Error parsing flight log {} for column '{}', message was {}", file.getName(), valueName, e.getMessage());
            return null;
        }
    }
}
