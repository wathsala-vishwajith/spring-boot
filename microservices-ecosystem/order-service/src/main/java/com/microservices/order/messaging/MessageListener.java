package com.microservices.order.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    @RabbitListener(queues = "order.queue")
    public void handleOrderEvents(String message) {
        System.out.println("Received order event: " + message);
    }

    @RabbitListener(queues = "product.queue")
    public void handleProductEvents(String message) {
        System.out.println("Received product event in Order Service: " + message);
    }
}
