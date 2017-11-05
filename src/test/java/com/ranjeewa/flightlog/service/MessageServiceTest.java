package com.ranjeewa.flightlog.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jms.Destination;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MessageServiceTest {

    @Mock
    private JmsMessagingTemplate template;

    @Mock
    private FlightLogService flightLogService;

    @InjectMocks
    private MessageService service;

    @Test
    public void publishWritesToQueue() throws Exception {
        String message = "savedFile1";
        service.publishFlightLogSaved(message);
        verify(template).convertAndSend(any(Destination.class), eq(message));
    }

    @Test
    public void consumedMessageIsHandled() throws Exception {
        String message = "savedFile2";
        service.processFlightLog(message);
        verify(flightLogService).saveFlightLogValues(message);
    }

}