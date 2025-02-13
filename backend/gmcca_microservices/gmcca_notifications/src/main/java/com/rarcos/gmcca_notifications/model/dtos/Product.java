package com.rarcos.gmcca_notifications.model.dtos;

import com.rarcos.gmcca_notifications.model.enums.DocProcessStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    private Long id;
    private String code;
    private String name;
    private String description;
    private Double price;
    private DocProcessStatus status;

    @Override
    public String toString(){
        return "Product -> id: " + id + ", name: " + name + ", description: " + description;
    }
}
