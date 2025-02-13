package com.rarcos.gmcca_inventory.services;

import com.rarcos.gmcca_inventory.model.dtos.BaseResponse;
import com.rarcos.gmcca_inventory.model.dtos.OrderItemRequest;
import com.rarcos.gmcca_inventory.model.entities.Inventory;
import com.rarcos.gmcca_inventory.repositories.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public boolean isInStock(String code) {
        Optional<Inventory> inventory = inventoryRepository.findByCode(code);

        return inventory.filter(v -> v.getQuantity() > 0).isPresent();
    }

    public BaseResponse areInStock(List<OrderItemRequest> orderItems) {
        List<String> errorList = new ArrayList<>();

        List<String> codes = orderItems.stream().map(OrderItemRequest::getCode).toList();

        List<Inventory> inventoryList = inventoryRepository.findByCodeIn(codes);

        orderItems.forEach(orderItem ->{
            Optional<Inventory> inventory = inventoryList.stream().filter(v -> v.getCode().equals(orderItem.getCode())).findFirst();
            if(inventory.isEmpty()) {
                errorList.add("Product with code " + orderItem.getCode() + " does not exist");
            } else if (inventory.get().getQuantity() < orderItem.getQuantity()) {
                errorList.add("Product with code " + orderItem.getCode() + " has insufficient quantity");
            }
        });

        return errorList.size() > 0 ? new BaseResponse(errorList.toArray(new String[0])) : new BaseResponse(null);
    }
}
