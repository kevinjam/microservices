package com.thinkdevs.ProductService.service;

import com.thinkdevs.ProductService.model.ProductRequest;
import com.thinkdevs.ProductService.model.ProductResponse;

public interface ProductService {
    Integer addProduct(ProductRequest request);

    ProductResponse getProductById(Integer id);

    void reduceQuantity(Integer productId, long quantity);
}
