package com.thinkdevs.PaymentService.service;

import com.thinkdevs.PaymentService.entity.TransactionDetails;
import com.thinkdevs.PaymentService.model.PaymentMode;
import com.thinkdevs.PaymentService.model.PaymentRequest;
import com.thinkdevs.PaymentService.model.PaymentResponse;
import com.thinkdevs.PaymentService.repository.TransactionDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final TransactionDetailsRepository repository;

    @Override
    public Integer doPayment(PaymentRequest paymentRequest) {
        log.info("Recording Payment Details : {} ", paymentRequest);
        TransactionDetails transactionDetails =
                TransactionDetails.builder()
                .paymentDate(LocalDateTime.now())
                .active(true)
                .amount(paymentRequest.getAmount())
                .paymentMode(paymentRequest.getPaymentMode().name())
                .paymentStatus("SUCCESS")
                .orderId(paymentRequest.getOrderId())
                .referenceNumber(paymentRequest.getReferenceNumber())
                .build();
         repository.save(transactionDetails);
        log.info("Transaction Completed with Id: {}", transactionDetails.getId());

        return transactionDetails.getId();
    }

    @Override
    public PaymentResponse getPaymentDetailsByOrderId(long orderId) {
        log.info("Getting payment details for order id {} ", orderId);
        TransactionDetails transactionDetails
                 = repository.findByOrderId(orderId);

        PaymentResponse paymentResponse
                 = PaymentResponse.builder()
                .paymentId(transactionDetails.getId())
                .paymentMode(PaymentMode.valueOf(transactionDetails.getPaymentMode()))
                .paymentDate(transactionDetails.getPaymentDate())
                .orderId(transactionDetails.getOrderId())
                .status(transactionDetails.getPaymentStatus())
                .amount(transactionDetails.getAmount())
                .build();

        return paymentResponse;
    }
}
