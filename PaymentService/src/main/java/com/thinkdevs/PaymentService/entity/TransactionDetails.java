package com.thinkdevs.PaymentService.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "transaction_details")
@Where(clause = "active <> 'false'")
public class TransactionDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private long orderId;

    private String paymentMode;

    private String referenceNumber;

    private LocalDateTime paymentDate;

    private String paymentStatus;

    private long amount;

    private Boolean active;
}
