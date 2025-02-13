package com.rarcos.gmcca_orders.events;

import com.rarcos.gmcca_orders.model.enums.OrderStatus;

public record OrderEvent (String orderNumber, int itemsCount, OrderStatus orderStatus){
}
