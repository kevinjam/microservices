package com.thinkdevs.OrderService.external.client;

import com.thinkdevs.OrderService.exception.CustomException;
import com.thinkdevs.OrderService.external.request.PaymentRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CircuitBreaker(name = "external", fallbackMethod = "fallback")
@FeignClient(name = "PAYMENT-SERVICE/payment")
public interface PaymentService {

    @PostMapping
     ResponseEntity<Integer> doPayment(@RequestBody PaymentRequest paymentRequest);


    default ResponseEntity<Integer> fallback(Exception e){
        throw new CustomException("Payment Service not available",
                "UNAVAILABLE",
                500);
    }
}
