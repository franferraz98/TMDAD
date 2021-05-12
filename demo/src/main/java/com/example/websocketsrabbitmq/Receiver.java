package com.example.websocketsrabbitmq;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.HtmlUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Controller
public class Receiver {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private TopicExchange exchange;

    @Autowired
    private Queue queue;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private ConnectionFactory connectionFactory;

    public Receiver(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;

    }

    @MessageMapping("/route")
    public void newBind (final Message message) {
        System.out.println("BINDEANDO: " + message.getText());
        String keyMask = message.getText();
        Binding bind = BindingBuilder.bind(queue).to(exchange).with(keyMask);
        rabbitAdmin.declareBinding(bind);
        RabbitTemplate rabbitTemplate = rabbitAdmin.getRabbitTemplate();
        while(rabbitAdmin.getQueueInfo(queue.getName()).getMessageCount() > 0){
            String result = (String) rabbitTemplate.receiveAndConvert(queue.getName());
            try{
                this.receiveMessage(result);
            } catch (Exception e){
                System.out.println("ERROR: " + e);
            }

        }
    }

    public void receiveMessage(String message) throws Exception {
        System.out.println("Received <" + message + ">");
        String[] parts = message.split(":::");
        String from = parts[0];
        String destination = "/topic/" +  parts[parts.length - 1];
        String text = "";
        for(int i = 1; i< parts.length-1; i++){
            text += parts[i];
        }
        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        simpMessagingTemplate.convertAndSend(destination,
                new OutputMessage(from, text, time));
    }
}