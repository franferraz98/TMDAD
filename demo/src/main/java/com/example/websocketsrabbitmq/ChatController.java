package com.example.websocketsrabbitmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class ChatController {

    @Autowired
    private Runner runner;

    @MessageMapping("/chat")
    public OutputMessage send(final Message message) throws Exception {
        System.out.println("Received: " + message.getFrom() + " -> " + message.getText());
        runner.run(message.getFrom() + ":::" + message.getText());
        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(message.getFrom(), message.getText(), time);
    }

}