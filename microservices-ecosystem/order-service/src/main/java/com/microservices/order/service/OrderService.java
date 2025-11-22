package com.microservices.order.service;

import com.microservices.order.client.ProductServiceClient;
import com.microservices.order.client.UserServiceClient;
import com.microservices.order.dto.OrderDTO;
import com.microservices.order.dto.ProductDTO;
import com.microservices.order.dto.UserDTO;
import com.microservices.order.messaging.MessagePublisher;
import com.microservices.order.model.Order;
import com.microservices.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private ProductServiceClient productServiceClient;

    @Autowired
    private MessagePublisher messagePublisher;

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public Optional<OrderDTO> getOrderById(Long id) {
        return orderRepository.findById(id).map(this::convertToDTO);
    }

    public OrderDTO createOrder(Order order) {
        UserDTO user = userServiceClient.getUserById(order.getUserId());
        ProductDTO product = productServiceClient.getProductById(order.getProductId());

        order.setTotalPrice(product.getPrice() * order.getQuantity());
        Order savedOrder = orderRepository.save(order);

        messagePublisher.publishOrderCreatedEvent(savedOrder);

        return convertToDTO(savedOrder);
    }

    public OrderDTO updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);

        messagePublisher.publishOrderUpdatedEvent(updatedOrder);

        return convertToDTO(updatedOrder);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
        messagePublisher.publishOrderDeletedEvent(id);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setQuantity(order.getQuantity());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setStatus(order.getStatus());
        dto.setOrderDate(order.getOrderDate());

        try {
            dto.setUser(userServiceClient.getUserById(order.getUserId()));
        } catch (Exception e) {
            System.err.println("Failed to fetch user: " + e.getMessage());
        }

        try {
            dto.setProduct(productServiceClient.getProductById(order.getProductId()));
        } catch (Exception e) {
            System.err.println("Failed to fetch product: " + e.getMessage());
        }

        return dto;
    }
}
