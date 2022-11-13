package com.thinkdevs.PaymentService.repository;

import com.thinkdevs.PaymentService.entity.TransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionDetailsRepository extends JpaRepository<TransactionDetails, Integer> {
    TransactionDetails findByOrderId(long orderId);
}
