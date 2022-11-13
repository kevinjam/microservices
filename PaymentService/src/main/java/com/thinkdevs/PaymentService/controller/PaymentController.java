package com.thinkdevs.PaymentService.controller;

import com.thinkdevs.PaymentService.model.PaymentRequest;
import com.thinkdevs.PaymentService.model.PaymentResponse;
import com.thinkdevs.PaymentService.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Integer> doPayment(@RequestBody PaymentRequest paymentRequest){
        return new ResponseEntity<>(
                paymentService.doPayment(paymentRequest),
                HttpStatus.OK
        );
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentDetails(@PathVariable("orderId") long orderId){

        return new ResponseEntity<>(
                paymentService.getPaymentDetailsByOrderId(orderId),
                HttpStatus.OK
        );
    }

}
