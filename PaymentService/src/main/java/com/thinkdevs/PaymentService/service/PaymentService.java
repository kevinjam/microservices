package com.thinkdevs.PaymentService.service;

import com.thinkdevs.PaymentService.model.PaymentRequest;
import com.thinkdevs.PaymentService.model.PaymentResponse;

public interface PaymentService {
    Integer doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(long orderId);
}
