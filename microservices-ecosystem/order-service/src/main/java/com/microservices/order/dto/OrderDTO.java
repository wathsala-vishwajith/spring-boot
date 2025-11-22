package com.microservices.order.dto;

import com.microservices.order.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private UserDTO user;
    private ProductDTO product;
    private Integer quantity;
    private Double totalPrice;
    private Order.OrderStatus status;
    private LocalDateTime orderDate;
}
