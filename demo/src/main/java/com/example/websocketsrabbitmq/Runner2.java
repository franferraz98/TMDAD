package com.example.websocketsrabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class Runner2 {

    private final RabbitTemplate rabbitTemplate;
    private final Receiver2 receiver2;

    public Runner2(Receiver2 receiver2, RabbitTemplate rabbitTemplate) {
        this.receiver2 = receiver2;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void run(String message) throws Exception {
        System.out.println("Sending message: <" + message + ">");
        rabbitTemplate.convertAndSend("spring-boot-exchange", "foo.bar.baz", message);
    }

}
