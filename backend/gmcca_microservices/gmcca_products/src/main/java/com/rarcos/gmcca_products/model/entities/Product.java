package com.rarcos.gmcca_products.model.entities;

import com.rarcos.gmcca_products.model.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String name;
    @Column(columnDefinition="TEXT")
    private String description;
    private Double price;
    private ProductStatus status;

    @Override
    public String toString(){
        return "Product -> id: " + id + ", name: " + name + ", description: " + description + ", status: " + status;
    }
}
