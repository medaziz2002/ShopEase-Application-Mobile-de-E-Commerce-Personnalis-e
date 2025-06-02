package com.example.ecommerce.dto;

import lombok.*;

import java.util.Date;
import java.util.List;



@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Integer id;
    private Integer userId;
    private Date orderDate;
    private Date deliveryDate;
    private String status;
    private String paymentMethod;
    private String deliveryAddress;
    private String deliveryMethod;
    private double totalAmount;
    private double deliveryCost;
    private List<OrderItemDTO> items;
}
