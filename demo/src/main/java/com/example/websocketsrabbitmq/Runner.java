package com.example.websocketsrabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class Runner {

    private final RabbitTemplate rabbitTemplate;
    private final Receiver receiver;

    public Runner(Receiver receiver, RabbitTemplate rabbitTemplate) {
        this.receiver = receiver;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void run(String message) throws Exception {
        System.out.println("Sending message: <" + message + ">");
        String[] parts = message.split(":::");
        String destination = "foo.bar." + parts[parts.length - 1];
        System.out.println("Destination: " + destination);
        rabbitTemplate.convertAndSend("spring-boot-exchange", destination, message);
    }

    public void toChatRoom(String message) throws Exception {
        System.out.println("Sending message: <" + message + "> to a room");
        String[] parts = message.split(":::");
        String exchange = parts[parts.length - 1];
        System.out.println("Destination: " + exchange);
        rabbitTemplate.convertAndSend(exchange, "", message);
    }

}
