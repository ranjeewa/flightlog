package com.ranjeewa.flightlog.service;

import com.ranjeewa.flightlog.domain.FlightLog;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class FlightLogParserTest {

    @Test
    public void canFindFirstAndLastValues() {

        String filePath = getClass().getResource("/test.txt").getFile();
        File file = new File(filePath);
        assertNotNull(file);
        assertTrue(file.exists());

        FlightLogParser parser = new FlightLogParser();

        FlightLog log = parser.findFirstAndLastValuesFromFile(file, "Elapsed Time (sec)");
        assertNotNull(log);
        assertEquals("3.018", log.getStartValue());
        assertEquals("159.118", log.getEndValue());
        assertEquals("test.txt", log.getFlightLogFileName());
    }

    @Test
    public void canHandleNonExistingValues() {
        String filePath = getClass().getResource("/test.txt").getFile();
        File file = new File(filePath);
        assertNotNull(file);
        assertTrue(file.exists());

        FlightLogParser parser = new FlightLogParser();

        FlightLog log = parser.findFirstAndLastValuesFromFile(file, "No Such Metric");
        assertNotNull(log);
        assertEquals(null, log.getStartValue());
        assertEquals(null, log.getEndValue());
        assertEquals("test.txt", log.getFlightLogFileName());
    }

}