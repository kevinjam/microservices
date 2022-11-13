package com.thinkdevs.ProductService.repository;

import com.thinkdevs.ProductService.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
