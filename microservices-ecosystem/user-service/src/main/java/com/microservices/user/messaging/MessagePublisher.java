package com.microservices.user.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.user.model.User;
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

    public void publishUserCreatedEvent(User user) {
        publishEvent("user.created", user);
    }

    public void publishUserUpdatedEvent(User user) {
        publishEvent("user.updated", user);
    }

    public void publishUserDeletedEvent(Long userId) {
        Map<String, Object> event = new HashMap<>();
        event.put("userId", userId);
        publishEvent("user.deleted", event);
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
