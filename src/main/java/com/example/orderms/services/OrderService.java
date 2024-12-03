package com.example.orderms.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.example.orderms.dtos.OrderCreatedEventDTO;
import com.example.orderms.dtos.OrderResponseDTO;
import com.example.orderms.entities.OrderEntity;
import com.example.orderms.entities.OrderItem;
import com.example.orderms.repositories.OrderRepository;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MongoTemplate mongoTemplate;

    public OrderService(OrderRepository orderRepository, MongoTemplate mongoTemplate) {
        this.orderRepository = orderRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public void save(OrderCreatedEventDTO event) {
        var entity = new OrderEntity();

        entity.setOrderId(event.codigoPedido());
        entity.setCustomerId(event.codigoCliente());
        entity.setItems(getOrderItems(event));

        entity.setTotal(getTotal(event));

        orderRepository.save(entity);
    }

    private BigDecimal getTotal(OrderCreatedEventDTO event) {
        return event.itens()
                .stream()
                .map(i -> i.preco().multiply(BigDecimal.valueOf(i.quantidade())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private static List<OrderItem> getOrderItems(OrderCreatedEventDTO event) {
        return event.itens().stream()
                .map(i -> new OrderItem(i.produto(), i.quantidade(), i.preco())).toList();
    }

    public Page<OrderResponseDTO> findAllByCustomerId(Long customerId, PageRequest pageRequest) {
        var orders = orderRepository.findAllByCustomerId(customerId, pageRequest);

        return orders.map(OrderResponseDTO::fromEntity);
    }

    public BigDecimal findTotalOnOrdersByCustomerId(Long customerId) {
        var aggregations = newAggregation(
                match(Criteria.where("customerId").is(customerId)),
                group().sum("total").as("total"));

        AggregationResults<Document> response = mongoTemplate.aggregate(aggregations, "tb_orders", Document.class);

        if (response.getMappedResults().isEmpty()) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(Objects.requireNonNull(response.getUniqueMappedResult()).get("total").toString());
    }
}
