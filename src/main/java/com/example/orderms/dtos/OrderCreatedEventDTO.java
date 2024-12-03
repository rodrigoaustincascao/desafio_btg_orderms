package com.example.orderms.dtos;

import java.util.List;

public record OrderCreatedEventDTO(Long codigoPedido,
        Long codigoCliente,
        List<OrderItemEventDTO> itens) {

}
