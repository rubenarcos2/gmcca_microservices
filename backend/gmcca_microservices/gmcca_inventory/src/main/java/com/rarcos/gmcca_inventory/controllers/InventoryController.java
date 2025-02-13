package com.rarcos.gmcca_inventory.controllers;

import com.rarcos.gmcca_inventory.model.dtos.BaseResponse;
import com.rarcos.gmcca_inventory.model.dtos.OrderItemRequest;
import com.rarcos.gmcca_inventory.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{code}")
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@PathVariable("code") String code){
        return inventoryService.isInStock(code);
    }

    @PostMapping("/in-stock")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse areInStock(@RequestBody List<OrderItemRequest> orderItems){
        return inventoryService.areInStock(orderItems);
    }
}
