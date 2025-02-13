package com.rarcos.gmcca_notifications.events;

import com.rarcos.gmcca_notifications.model.enums.OrderStatus;

public record OrderEvent(String orderNumber, int itemsCount, OrderStatus orderStatus){
}
