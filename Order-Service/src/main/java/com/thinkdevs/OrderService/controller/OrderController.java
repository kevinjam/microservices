package com.thinkdevs.OrderService.controller;

import com.thinkdevs.OrderService.model.OrderRequest;
import com.thinkdevs.OrderService.model.OrderResponse;
import com.thinkdevs.OrderService.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@Log4j2
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PreAuthorize("hasAnyAuthority('Customer')")
     @PostMapping("/placeOrder")
    public ResponseEntity<Integer> placeOrder(@RequestBody OrderRequest request) {
         Integer orderId = orderService.placeOrder(request);
         log.info("Order Id {} ", orderId);
        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('Admin') || hasAnyAuthority('Customer')")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable Integer orderId) {
        OrderResponse orderResponse
                = orderService.getOrderDetails(orderId);

        return new ResponseEntity<>(orderResponse,
                HttpStatus.OK);
    }
}
