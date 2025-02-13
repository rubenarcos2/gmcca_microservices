package com.rarcos.gmcca_orders.model.dtos;

import com.rarcos.gmcca_orders.model.entities.Order;

import java.util.List;

public record OrderResponse(Long id, String orderNumber, List<OrderItemsResponse> orderItems) {
}
