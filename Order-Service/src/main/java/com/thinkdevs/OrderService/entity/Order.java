package com.thinkdevs.OrderService.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_details")
@Where(clause = "active <> 'false'")
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer productId;

    private long quantity;

    private LocalDateTime orderDate;

    private String orderStatus;
    private long amount;
    private Boolean active;
}
