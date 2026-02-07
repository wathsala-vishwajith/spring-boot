package com.microservices.order.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.order.model.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MessagePublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String EXCHANGE = "microservices.exchange";

    public void publishOrderCreatedEvent(Order order) {
        publishEvent("order.created", order);
    }

    public void publishOrderUpdatedEvent(Order order) {
        publishEvent("order.updated", order);
    }

    public void publishOrderDeletedEvent(Long orderId) {
        Map<String, Object> event = new HashMap<>();
        event.put("orderId", orderId);
        publishEvent("order.deleted", event);
    }

    private void publishEvent(String routingKey, Object payload) {
        try {
            String message = objectMapper.writeValueAsString(payload);
            rabbitTemplate.convertAndSend(EXCHANGE, routingKey, message);
            System.out.println("Published event: " + routingKey + " - " + message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
