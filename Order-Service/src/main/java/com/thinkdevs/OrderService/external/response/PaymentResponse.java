package com.thinkdevs.OrderService.external.response;

import com.thinkdevs.OrderService.model.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {
    private Integer paymentId;
    private String status;
    private PaymentMode paymentMode;
    private long amount;
    private LocalDateTime paymentDate;
    private long orderId;
}
