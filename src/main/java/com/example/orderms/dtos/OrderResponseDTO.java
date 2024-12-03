package com.example.orderms.dtos;

import java.math.BigDecimal;

import com.example.orderms.entities.OrderEntity;

public record OrderResponseDTO(Long orderId, Long customerId, BigDecimal total) {

    public static OrderResponseDTO fromEntity(OrderEntity entity) {
        return new OrderResponseDTO(entity.getOrderId(), entity.getCustomerId(), entity.getTotal());
    }
}
