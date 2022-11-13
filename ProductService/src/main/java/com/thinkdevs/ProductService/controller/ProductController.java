package com.thinkdevs.ProductService.controller;

import com.thinkdevs.ProductService.model.ProductRequest;
import com.thinkdevs.ProductService.model.ProductResponse;
import com.thinkdevs.ProductService.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PreAuthorize("hasAnyAuthority('Admin')")
    @PostMapping
    public ResponseEntity<Integer> addProduct(@RequestBody ProductRequest request){
        Integer productId = productService.addProduct(request);
       return new ResponseEntity<>(productId, HttpStatus.CREATED);
    }

@PreAuthorize("hasAnyAuthority('Admin') || hasAnyAuthority('Customer') || hasAnyAuthority('SCOPE_internal')")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("id") Integer id){
        ProductResponse productResponse
                = productService.getProductById(id);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @PutMapping("/reduceQuantity/{id}")
    public ResponseEntity<Void> reduceQuantity(
            @PathVariable("id") Integer productId,
            @RequestParam long quantity
    ){
        productService.reduceQuantity(productId, quantity);
        return new ResponseEntity<>(HttpStatus.OK);

    }
}
