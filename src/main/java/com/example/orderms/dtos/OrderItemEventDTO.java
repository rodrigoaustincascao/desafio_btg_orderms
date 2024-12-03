package com.example.orderms.dtos;

import java.math.BigDecimal;

public record OrderItemEventDTO(String produto,
        Integer quantidade,
        BigDecimal preco) {
}
