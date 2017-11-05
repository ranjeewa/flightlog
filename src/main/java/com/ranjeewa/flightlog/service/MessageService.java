package com.ranjeewa.flightlog.service;

import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Description;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@EnableJms
@Description("A service to produce and consume messages from a JMS queue")
public class MessageService {

    private final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final ActiveMQQueue queue;
    private final FlightLogService flightLogService;

    private final JmsMessagingTemplate jmsMessagingTemplate;

    MessageService(FlightLogService flightLogService, JmsMessagingTemplate jmsMessagingTemplate) {
        this.flightLogService = flightLogService;
        this.jmsMessagingTemplate = jmsMessagingTemplate;
        this.queue = new ActiveMQQueue("flightlog.messages");
    }

    public void publishFlightLogSaved(String fileName) {
        jmsMessagingTemplate.convertAndSend(this.queue, fileName);
        logger.info("Published message for flight log {}", fileName);
    }

    @JmsListener(destination = "flightlog.messages")
    public void processFlightLog(String text) {
        logger.info("Received message for flight log {}", text);
        flightLogService.saveFlightLogValues(text);
    }

}
