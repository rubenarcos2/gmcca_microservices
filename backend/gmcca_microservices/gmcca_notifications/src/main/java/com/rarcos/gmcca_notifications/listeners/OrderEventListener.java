package com.rarcos.gmcca_notifications.listeners;

import com.rarcos.gmcca_notifications.events.OrderEvent;
import com.rarcos.gmcca_notifications.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    public OrderEventListener(SimpMessagingTemplate messagingTemplate){
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(topics = "order-topic")
    public void handleOrdersNotifications(String message) {
        var orderEvent = JsonUtils.fromJson(message, OrderEvent.class);

        messagingTemplate.convertAndSend("/topic/messages", "El pedido con número "+orderEvent.orderNumber()+" se encuentra en estado "+orderEvent.orderStatus()+ " y contiene "+orderEvent.itemsCount()+" productos");

        log.info("Order {} event received for order : {} with {} items", orderEvent.orderStatus(), orderEvent.orderNumber(), orderEvent.itemsCount());
    }
}
