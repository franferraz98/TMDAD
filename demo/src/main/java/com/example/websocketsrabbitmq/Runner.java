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
        rabbitTemplate.convertAndSend("spring-boot-exchange", "foo.bar.baz", message);
    }

}