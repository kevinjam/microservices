package com.thinkdevs.ProductService.service;

import com.thinkdevs.ProductService.entity.Product;
import com.thinkdevs.ProductService.exception.ProductServiceCustomException;
import com.thinkdevs.ProductService.model.ProductRequest;
import com.thinkdevs.ProductService.model.ProductResponse;
import com.thinkdevs.ProductService.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;
    @Override
    public Integer addProduct(ProductRequest request) {
        log.info("Adding Product..");
        Product product
                = Product.builder()
                .productName(request.getName())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .active(true)
                .build();
         productRepository.save(product);
        log.info("Product Created");
        return product.getId();
    }

    @Override
    public ProductResponse getProductById(Integer id) {
        log.info("Get the product for productId: {}", id);

        var product = productRepository
                .findById(id)
                .orElseThrow(()->new ProductServiceCustomException("Product with the id not found", "Product Not found"));
        ProductResponse response
                = new ProductResponse();
        copyProperties(product, response);
        return response;
    }

    @Override
    public void reduceQuantity(Integer productId, long quantity) {
        log.info("Reduce Quantity {} for id {} ", quantity, productId);

        Product product
                = productRepository.findById(productId)
                .orElseThrow(()->new ProductServiceCustomException(
                        "Product with given Id not found ", "PRODUCT_NOT_FOUND"
                ));

        if (product.getQuantity() < quantity) {
            throw new ProductServiceCustomException(
                    "Product does not have sufficient Quantity",
                    "INSUFFICIENT_QUANTITY"
            );

        }
        product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(getProduct(product));
            log.info("Product Quantity updated Successfully");

    }

    private Product getProduct(Product product) {
        return product;
    }
}
