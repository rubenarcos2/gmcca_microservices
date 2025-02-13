package com.rarcos.gmcca_products.controller;

import com.rarcos.gmcca_products.model.dtos.ProductRequest;
import com.rarcos.gmcca_products.model.dtos.ProductResponse;
import com.rarcos.gmcca_products.model.entities.Product;
import com.rarcos.gmcca_products.model.enums.ProductStatus;
import com.rarcos.gmcca_products.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addProduct(@RequestBody ProductRequest productRequest){
        this.productService.addProduct(productRequest);

    }

    @CrossOrigin
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts(){
        return this.productService.getAllProducts();
    }

    @CrossOrigin
    @GetMapping("/code")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse getProductByCode(@RequestParam String code){
        return this.productService.getProductByCode(code);
    }

    @PostMapping("/status")
    @ResponseStatus(HttpStatus.OK)
    public void changeStatusProduct(@RequestParam String code, @RequestParam ProductStatus status){
        this.productService.changeStatus(code, status);
    }
}
