package com.ranjeewa.flightlog.service;

import com.ranjeewa.flightlog.domain.FlightLog;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.mockito.Mockito.*;

public class FlightLogServiceTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void parsedValuesAreSavedToRepo() throws Exception {

        final FileService mockFileService = mock(FileService.class);
        final ServiceProperties serviceProperties = new ServiceProperties();
        final FlightLogRepository mockRepository = mock(FlightLogRepository.class);
        final FlightLogParser mockParser = mock(FlightLogParser.class);

        serviceProperties.setParamName("anyParam");

        File mockFile = mock(File.class);
        when(mockFile.exists()).thenReturn(true);
        when(mockFileService.findFile(anyString())).thenReturn(mockFile);
        FlightLog flightLog = new FlightLog();
        when(mockParser.findFirstAndLastValuesFromFile(mockFile, serviceProperties.getParamName()))
                .thenReturn(flightLog);

        FlightLogService flightLogService = new FlightLogService(serviceProperties, mockRepository,
                mockFileService, mockParser);
        flightLogService.saveFlightLogValues("file");

        verify(mockRepository).save(flightLog);
    }

    @Test
    public void emptyFileDoesntCauseSave() throws Exception {

        final FileService mockFileService = mock(FileService.class);
        final ServiceProperties serviceProperties = new ServiceProperties();
        final FlightLogRepository mockRepository = mock(FlightLogRepository.class);
        final FlightLogParser mockParser = mock(FlightLogParser.class);

        serviceProperties.setParamName("anyParam");
        when(mockFileService.findFile(anyString())).thenReturn(null);

        FlightLogService flightLogService = new FlightLogService(serviceProperties, mockRepository,
                mockFileService, mockParser);
        flightLogService.saveFlightLogValues("file");

        verify(mockRepository, never()).save(any(FlightLog.class));
    }

}