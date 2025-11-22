package com.microservices.product.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.product.model.Product;
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

    public void publishProductCreatedEvent(Product product) {
        publishEvent("product.created", product);
    }

    public void publishProductUpdatedEvent(Product product) {
        publishEvent("product.updated", product);
    }

    public void publishProductStockUpdatedEvent(Product product) {
        publishEvent("product.stock.updated", product);
    }

    public void publishProductDeletedEvent(Long productId) {
        Map<String, Object> event = new HashMap<>();
        event.put("productId", productId);
        publishEvent("product.deleted", event);
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
