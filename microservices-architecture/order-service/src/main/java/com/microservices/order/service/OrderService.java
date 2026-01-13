package com.microservices.order.service;

import com.microservices.order.messaging.OrderEventPublisher;
import com.microservices.order.model.Order;
import com.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final OrderEventPublisher eventPublisher;

    public Order createOrder(Order order) {
        ProductDTO product = productClient.getProduct(order.getProductId());
        order.setProductName(product.getName());
        order.setTotalPrice(product.getPrice() * order.getQuantity());

        Order savedOrder = orderRepository.save(order);
        eventPublisher.publishOrderCreated(savedOrder);

        return savedOrder;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Order updateOrderStatus(Long id, String status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        eventPublisher.publishOrderStatusChanged(updatedOrder);
        return updatedOrder;
    }

    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    public List<Order> getOrdersByCustomer(String email) {
        return orderRepository.findByCustomerEmail(email);
    }
}
