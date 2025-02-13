package com.rarcos.gmcca_orders.services;

import com.rarcos.gmcca_orders.events.OrderEvent;
import com.rarcos.gmcca_orders.model.dtos.*;
import com.rarcos.gmcca_orders.model.entities.Order;
import com.rarcos.gmcca_orders.model.entities.OrderItems;
import com.rarcos.gmcca_orders.model.enums.OrderStatus;
import com.rarcos.gmcca_orders.repositories.OrderRepository;
import com.rarcos.gmcca_orders.utils.JsonUtils;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObservationRegistry observationRegistry;

    public OrderResponse placeOrder(OrderRequest orderRequest) {

        Observation inventoryObservation = Observation.createNotStarted("inventory-service", observationRegistry);

        //Check for inventory
        return inventoryObservation.observe(() -> {
            BaseResponse result = this.webClientBuilder.build()
                    .post()
                    .uri("http://localhost:8081/api/inventory/in-stock")
                    .bodyValue(orderRequest.getOrderItems())
                    .retrieve()
                    .bodyToMono(BaseResponse.class)
                    .block();

            if (result != null && !result.hasErrors()) {
                Order order = new Order();
                order.setOrderNumber(UUID.randomUUID().toString());
                order.setOrderItems(orderRequest.getOrderItems().stream().map(orderItemRequest -> mapOrderItemRequestToOrderItem(orderItemRequest, order)).toList());
                Order savedOrder = this.orderRepository.save(order);

                //Send message to notification service
                this.kafkaTemplate.send("order-topic", JsonUtils.toJson(
                        new OrderEvent(savedOrder.getOrderNumber(), savedOrder.getOrderItems().size(), OrderStatus.PLACED)
                ));

                return mapToOrderResponse(savedOrder);

            } else {
                throw new IllegalArgumentException("Some of the products are not in stock");
            }
        });
    }

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = this.orderRepository.findAll();

        return orders.stream().map(this::mapToOrderResponse).toList();
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(order.getId(), order.getOrderNumber(), order.getOrderItems().stream().map(this::mapToOrderItemRequest).toList());
    }

    private OrderItemsResponse mapToOrderItemRequest(OrderItems orderItems) {
        return new OrderItemsResponse(orderItems.getId(), orderItems.getCode(), orderItems.getPrice(), orderItems.getQuantity());
    }

    private OrderItems mapOrderItemRequestToOrderItem(OrderItemRequest orderItemRequest, Order order) {
        return OrderItems.builder()
                .id(orderItemRequest.getId())
                .code(orderItemRequest.getCode())
                .price(orderItemRequest.getPrice())
                .quantity(orderItemRequest.getQuantity())
                .order(order)
                .build();
    }
}
