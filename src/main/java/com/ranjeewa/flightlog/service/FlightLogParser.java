package com.ranjeewa.flightlog.service;

import com.ranjeewa.flightlog.domain.FlightLog;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

@Component
public class FlightLogParser {

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
            //TODO handle this
            e.printStackTrace();
            return null;
        }
    }
}
