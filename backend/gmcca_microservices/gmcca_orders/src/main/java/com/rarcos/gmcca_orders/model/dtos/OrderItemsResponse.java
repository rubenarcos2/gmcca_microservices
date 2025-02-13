package com.rarcos.gmcca_orders.model.dtos;

import java.util.List;

public record OrderItemsResponse(Long id, String code, Double price, Long quantity) {
}
