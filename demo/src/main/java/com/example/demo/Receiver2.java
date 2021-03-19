package com.example.demo;
import org.springframework.stereotype.Component;

@Component
public class Receiver2 {

    public void receiveMessage(String message) {
        System.out.println("Received <" + message + ">");
    }

}