package com.rarcos.gmcca_orders.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
    private Long id;
    private String code;
    private Double price;
    private Long quantity;
}
