package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.OrderDTO;
import com.example.ecommerce.dto.OrderItemDTO;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;

    public OrderMapper(OrderItemMapper orderItemMapper) {
        this.orderItemMapper = orderItemMapper;
    }

    public OrderDTO toDto(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .orderDate(order.getOrderDate())
                .deliveryDate(order.getDeliveryDate())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryMethod(order.getDeliveryMethod())
                .totalAmount(order.getTotalAmount())
                .deliveryCost(order.getDeliveryCost())
                .items(Optional.ofNullable(order.getItems())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(orderItemMapper::toDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public Order toEntity(OrderDTO orderDTO) {
        Order order = Order.builder()
                .id(orderDTO.getId())
                .orderDate(new Date())
                .deliveryDate(orderDTO.getDeliveryDate())
                .status(orderDTO.getStatus())
                .paymentMethod(orderDTO.getPaymentMethod())
                .deliveryAddress(orderDTO.getDeliveryAddress())
                .deliveryMethod(orderDTO.getDeliveryMethod())
                .totalAmount(orderDTO.getTotalAmount())
                .deliveryCost(orderDTO.getDeliveryCost())
                .build();

        if (orderDTO.getItems() != null) {
            List<OrderItem> items = orderDTO.getItems().stream()
                    .map(dto -> {
                        OrderItem item = orderItemMapper.toEntity(dto);
                        item.setOrder(order); // très important pour maintenir la relation bidirectionnelle
                        return item;
                    })
                    .collect(Collectors.toList());
            order.setItems(items);
        }

        return order;
    }
}

