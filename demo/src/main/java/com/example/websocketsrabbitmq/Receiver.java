package com.example.websocketsrabbitmq;


import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Controller
public class Receiver {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private List<String> allQueueNames;

    @Autowired
    private TopicExchange exchange;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private SimpleMessageListenerContainer listenerContainer;

    public Receiver(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.allQueueNames = new ArrayList<String>();
    }

    @MessageMapping("/addToRoom")
    public void addToRoom (final Message message){
        String text = message.getText();
        System.out.println(text);
        String[] parts = text.split(":::");
        String room = parts[0];
        String user = parts[1];

        Queue queue = new Queue(user, true);
        FanoutExchange exchange = new FanoutExchange(room);
        Binding binding = BindingBuilder.bind(queue).to(exchange);

        rabbitAdmin.declareBinding(binding);

        System.out.println("No destruyo todo");
    }

    @MessageMapping("/createRoom")
    public void createChatRoom (final Message message){
        String queueName = message.getFrom();
        String exchangeName = message.getText();

        FanoutExchange exchange = new FanoutExchange(exchangeName);
        Queue queue = new Queue(queueName, true);
        Binding binding = BindingBuilder.bind(queue).to(exchange); //TODO: No se yo

        // rabbitAdmin.declareExchange(exchange);
        // rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(binding);
    }

    @MessageMapping("/route")
    public void newBind (final Message message) {
        String queueName = message.getText();
        allQueueNames.add(queueName);
        String keyMask = "foo.bar." + message.getText();
        org.springframework.amqp.core.Queue queue = new org.springframework.amqp.core.Queue(queueName, true);
        Binding bind = BindingBuilder.bind(queue).to(exchange).with(keyMask);
        rabbitAdmin.declareQueue(queue);
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

        String[] namesArray = allQueueNames.toArray(new String[0]);
        listenerContainer.setQueueNames(namesArray);
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