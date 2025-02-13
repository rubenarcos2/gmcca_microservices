package com.rarcos.gmcca_products.services;

import com.rarcos.gmcca_products.model.dtos.ProductRequest;
import com.rarcos.gmcca_products.model.dtos.ProductResponse;
import com.rarcos.gmcca_products.model.entities.Product;
import com.rarcos.gmcca_products.model.enums.ProductStatus;
import com.rarcos.gmcca_products.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void addProduct(ProductRequest productRequest){
        Product product = Product.builder()
                            .code(productRequest.getCode())
                            .name(productRequest.getName())
                            .description(productRequest.getDescription())
                            .price(productRequest.getPrice())
                            .status(ProductStatus.DISABLE)
                            .build();
        productRepository.save(product);

        log.info("Product added: {}", product);
    }

    public List<ProductResponse> getAllProducts(){
        List<Product> products = productRepository.findAll();

        return products.stream().map(this::mapToProductResponse).toList();
    }

    public ProductResponse getProductByCode(String code){
        Optional<Product> product = productRepository.findByCode(code);

        if(product.isPresent()){
            return ProductResponse.builder()
                    .id(product.get().getId())
                    .code(product.get().getCode())
                    .name(product.get().getName())
                    .description(product.get().getDescription())
                    .price(product.get().getPrice())
                    .status(product.get().getStatus())
                    .build();
        }else
            return null;

    }

    public void changeStatus(String code, ProductStatus status){
        Optional<Product> product = productRepository.findByCode(code);
        if(product.isPresent()){
            product.get().setStatus(status);
            productRepository.save(product.get());

            log.info("Product status modify: {}", product);
        }

    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .status(product.getStatus())
                .build();
    }
}
