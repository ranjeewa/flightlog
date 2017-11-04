package com.ranjeewa.flightlog.controllers;

import com.ranjeewa.flightlog.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Description;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.*;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@Description("A controller for saving, retrieving and querying flight logs")
public class FlightLogController {

    private final static Logger logger = LoggerFactory.getLogger(FlightLogController.class);

    private final FileService fileService;

    FlightLogController(FileService fileService) {
        this.fileService = fileService;
    }

    @RequestMapping(method = GET, path = "/flights/{id}")
    public ResponseEntity<Resource> downloadFlightLog(@PathVariable("id") String fileName) throws IOException {

        File flightLog = fileService.findFile(fileName);

        if (flightLog != null) {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(flightLog));
            return ResponseEntity.ok().contentLength(flightLog.length()).contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
        } else {
            logger.info("Couldn't find requested flight log {}", fileName);
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(method = POST, path= "/flights", produces = "application/json")
    public @ResponseBody Map<String, String> uploadFlightLog(InputStream flightLog) throws IOException {

        String fileName = fileService.saveFile(flightLog);
        //TODO fire off parsing request
        return Collections.singletonMap("resourceId", "/flights/" + fileName);
    }

    @ExceptionHandler(IOException.class)
    void handleFileSaveFailure(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error saving flight log, please try again");
    }

}
