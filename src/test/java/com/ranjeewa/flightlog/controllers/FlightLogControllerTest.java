package com.ranjeewa.flightlog.controllers;

import com.ranjeewa.flightlog.domain.FlightLog;
import com.ranjeewa.flightlog.service.FileService;
import com.ranjeewa.flightlog.service.FlightLogRepository;
import com.ranjeewa.flightlog.service.MessageService;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FlightLogControllerTest {

    private static FlightLogRepository mockRepository = mock(FlightLogRepository.class);


    @Test
    public void uploadReturnsFileName() throws Exception {
        MessageService mockMessageService = mock(MessageService.class);

        FileService mockFileService = mock(FileService.class);
        String savedFileName = "newFileName.log";

        when(mockFileService.saveFile(any())).thenReturn(savedFileName);

        FlightLogController controller = new FlightLogController(mockFileService, mockRepository, mockMessageService);
        ResponseEntity<Map<String, String>> response = controller.uploadFlightLog(new ByteArrayInputStream("flightlog".getBytes()));
        assertNotNull(response);
        assertTrue(response.getBody().get("resourceId").contains(savedFileName));
    }

    @Test
    public void savedFileTriggersJMSMessage() throws Exception {
        MessageService mockMessageService = mock(MessageService.class);

        FileService mockFileService = mock(FileService.class);
        String savedFileName = "newFileName.log";

        when(mockFileService.saveFile(any())).thenReturn(savedFileName);

        FlightLogController controller = new FlightLogController(mockFileService, mockRepository, mockMessageService);
        ResponseEntity<Map<String, String>> response = controller.uploadFlightLog(new ByteArrayInputStream("flightlog".getBytes()));
        verify(mockMessageService).publishFlightLogSaved(savedFileName);
    }

    @Test
    public void noJMSMessageIfFileNotSaved() throws Exception {
        MessageService mockMessageService = mock(MessageService.class);

        FileService mockFileService = mock(FileService.class);
        String savedFileName = "newFileName.log";

        when(mockFileService.saveFile(any())).thenReturn(null);

        FlightLogController controller = new FlightLogController(mockFileService, mockRepository, mockMessageService);
        ResponseEntity<Map<String, String>> response = controller.uploadFlightLog(new ByteArrayInputStream("flightlog".getBytes()));
        verify(mockMessageService, never()).publishFlightLogSaved(savedFileName);
    }

    @Test
    public void fileSaveFailureReturnsError() throws Exception {
        MessageService mockMessageService = mock(MessageService.class);

        FileService mockFileService = mock(FileService.class);
        when(mockFileService.saveFile(any())).thenThrow(new IOException("failed to save"));

        try {
            FlightLogController controller = new FlightLogController(mockFileService, mockRepository, mockMessageService);
            ResponseEntity<Map<String, String>> response = controller.uploadFlightLog(new ByteArrayInputStream("flightlog".getBytes()));
            fail("Should throw exception");
        } catch (Exception e) {
            assertTrue(e instanceof IOException);
        }
    }

    @Test
    public void downloadRetrievesExistingFile() throws Exception {
        MessageService mockMessageService = mock(MessageService.class);

        File file = new File(System.getProperty("java.io.tmpdir") + "tempfile");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileService mockFileService = mock(FileService.class);
        when(mockFileService.findFile(any())).thenReturn(file);

        FlightLogController controller = new FlightLogController(mockFileService, mockRepository, mockMessageService);

        ResponseEntity responseEntity = controller.downloadFlightLog("log123");
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());
        assertEquals(0L, responseEntity.getHeaders().getContentLength());
    }

    @Test
    public void downloadNonExistingFileReturns404() throws Exception {
        MessageService mockMessageService = mock(MessageService.class);

        FileService mockFileService = mock(FileService.class);
        when(mockFileService.findFile(any())).thenReturn(null);

        FlightLogController controller = new FlightLogController(mockFileService, mockRepository, mockMessageService);

        ResponseEntity responseEntity = controller.downloadFlightLog("noSuchLog");
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND.value(), responseEntity.getStatusCodeValue());
    }

    @Test
    public void nonExistingFileReturnsNoStats() throws Exception {

        String noFile = "noSuchFile";

        MessageService mockMessageService = mock(MessageService.class);
        FileService mockFileService = mock(FileService.class);
        FlightLogRepository repository = mock(FlightLogRepository.class);
        when(repository.findByFlightLogFileName(noFile)).thenReturn(null);

        FlightLogController controller = new FlightLogController(mockFileService, repository, mockMessageService);

        ResponseEntity<Map<String, String>> responseEntity = controller.findBatteryValues(noFile);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND.value(), responseEntity.getStatusCodeValue());
    }

    @Test
    public void canGetStatsForExistingFile() throws Exception {

        String existingFile = "myFile";

        FlightLog model = new FlightLog();
        model.setFlightLogFileName(existingFile);
        model.setStartValue("100");
        model.setEndValue("0");

        MessageService mockMessageService = mock(MessageService.class);
        FileService mockFileService = mock(FileService.class);
        FlightLogRepository repository = mock(FlightLogRepository.class);
        when(repository.findByFlightLogFileName(existingFile)).thenReturn(model);

        FlightLogController controller = new FlightLogController(mockFileService, repository, mockMessageService);

        ResponseEntity<Map<String, String>> responseEntity = controller.findBatteryValues(existingFile);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());
        Map<String, String> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals("/flights/"+existingFile, responseBody.get("resourceId"));
        assertTrue(responseBody.containsKey("startValue"));
        assertTrue(responseBody.containsKey("endValue"));
    }
}