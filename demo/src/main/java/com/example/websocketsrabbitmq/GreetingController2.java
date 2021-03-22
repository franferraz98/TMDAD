package com.example.websocketsrabbitmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller

public class GreetingController2 {

    @Autowired
    private Runner2 runner;

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting2 greeting(HelloMessage2 message) throws Exception {
        System.out.println("NAME: " + message.getName());
        runner.run(message.getName());
        return new Greeting2("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

}