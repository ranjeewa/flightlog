package com.ranjeewa.flightlog.controllers;

import com.ranjeewa.flightlog.service.FileService;
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

    @Test
    public void uploadReturnsFileName() throws Exception {

        String savedFile = "newFileName.log";

        FileService service = mock(FileService.class);
        when(service.saveFile(any())).thenReturn(savedFile);

        FlightLogController controller = new FlightLogController(service);
        Map<String, String> response = controller.uploadFlightLog(new ByteArrayInputStream("flightlog".getBytes()));
        assertNotNull(response);
        assertEquals("/flights/"+ savedFile, response.get("resourceId"));
    }

    @Test
    public void failureReturnsError() throws Exception {

        FileService service = mock(FileService.class);
        when(service.saveFile(any())).thenThrow(new IOException("failed to save"));

        try {
            FlightLogController controller = new FlightLogController(service);
            Map<String, String> response = controller.uploadFlightLog(new ByteArrayInputStream("flightlog".getBytes()));
            fail("Should throw exception");
        } catch (Exception e) {
            assertTrue(e instanceof IOException);
        }
    }

    @Test
    public void downloadRetrievesExistingFile() throws Exception {

        File file = new File(System.getProperty("java.io.tmpdir") + "tempfile");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileService mockFileService = mock(FileService.class);
        when(mockFileService.findFile(any())).thenReturn(file);

        FlightLogController controller = new FlightLogController(mockFileService);

        ResponseEntity responseEntity = controller.downloadFlightLog("log123");
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());
        assertEquals(0L, responseEntity.getHeaders().getContentLength());
    }

    @Test
    public void downloadNonExistingFileReturns404() throws Exception {

        FileService mockFileService = mock(FileService.class);
        when(mockFileService.findFile(any())).thenReturn(null);

        FlightLogController controller = new FlightLogController(mockFileService);

        ResponseEntity responseEntity = controller.downloadFlightLog("log123");
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND.value(), responseEntity.getStatusCodeValue());
    }

}