package com.example.orderms.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.example.orderms.dtos.OrderCreatedEventDTO;
import com.example.orderms.services.OrderService;

import static com.example.orderms.config.RabbitMqConfig.ORDER_CREATED_QUEUE;

@Component
public class OrderCreatedListener {

    private final OrderService orderService;

    public OrderCreatedListener(OrderService orderService) {
        this.orderService = orderService;
    }

    private final Logger logger = LoggerFactory.getLogger(OrderCreatedListener.class);

    @RabbitListener(queues = ORDER_CREATED_QUEUE)
    public void listen(Message<OrderCreatedEventDTO> message) {
        logger.info("Message consumed: {}", message);

        orderService.save(message.getPayload());
    }
}
