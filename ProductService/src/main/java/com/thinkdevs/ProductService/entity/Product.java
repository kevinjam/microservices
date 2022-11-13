package com.thinkdevs.ProductService.entity;

import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "product")
@Where(clause = "active <> 'false'")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String productName;

    private long price;

    private long quantity;

    private Boolean active ;
}
