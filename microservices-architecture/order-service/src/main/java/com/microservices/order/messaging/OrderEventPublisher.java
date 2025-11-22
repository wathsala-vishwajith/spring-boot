package com.microservices.order.messaging;

import com.microservices.order.config.RabbitMQConfig;
import com.microservices.order.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishOrderCreated(Order order) {
        log.info("Publishing order created event for order id: {}", order.getId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_ROUTING_KEY,
                new OrderEvent("ORDER_CREATED", order)
        );
    }

    public void publishOrderStatusChanged(Order order) {
        log.info("Publishing order status changed event for order id: {}", order.getId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                "order.status.changed",
                new OrderEvent("ORDER_STATUS_CHANGED", order)
        );
    }
}

class OrderEvent {
    private String eventType;
    private Order order;

    public OrderEvent() {}

    public OrderEvent(String eventType, Order order) {
        this.eventType = eventType;
        this.order = order;
    }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
}
