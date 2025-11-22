package com.microservices.notification.messaging;

import com.microservices.notification.config.RabbitMQConfig;
import com.microservices.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleOrderEvent(Map<String, Object> event) {
        try {
            String eventType = (String) event.get("eventType");
            Map<String, Object> order = (Map<String, Object>) event.get("order");

            log.info("Received order event: {}", eventType);

            String recipient = (String) order.get("customerEmail");
            Long orderId = ((Number) order.get("id")).longValue();
            String status = (String) order.get("status");

            String subject;
            String message;

            if ("ORDER_CREATED".equals(eventType)) {
                subject = "Order Confirmation - Order #" + orderId;
                message = String.format("Your order #%d has been created successfully. Status: %s", orderId, status);
            } else if ("ORDER_STATUS_CHANGED".equals(eventType)) {
                subject = "Order Status Update - Order #" + orderId;
                message = String.format("Your order #%d status has been updated to: %s", orderId, status);
            } else {
                subject = "Order Notification - Order #" + orderId;
                message = String.format("Update on your order #%d", orderId);
            }

            notificationService.sendNotification(recipient, subject, message, "EMAIL");

        } catch (Exception e) {
            log.error("Error processing order event", e);
        }
    }
}
