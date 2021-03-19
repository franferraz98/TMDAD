package com.example.demo;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MessageLauncher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Async
    public void sendMessage(String message) throws InterruptedException {
        Thread.sleep(5000);
        rabbitTemplate.convertAndSend("spring-boot-exchange", "foo.bar.baz", message);
    }
}
