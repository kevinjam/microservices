package com.thinkdevs.OrderService.service;


import com.thinkdevs.OrderService.entity.Order;
import com.thinkdevs.OrderService.exception.CustomException;
import com.thinkdevs.OrderService.external.client.PaymentService;
import com.thinkdevs.OrderService.external.client.ProductService;
import com.thinkdevs.OrderService.external.request.PaymentRequest;
import com.thinkdevs.OrderService.external.response.PaymentResponse;
import com.thinkdevs.OrderService.model.OrderRequest;
import com.thinkdevs.OrderService.model.OrderResponse;
import com.thinkdevs.OrderService.model.PaymentMode;
import com.thinkdevs.OrderService.model.ProductResponse;
import com.thinkdevs.OrderService.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@Slf4j
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductService productService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    OrderService orderService = new OrderServiceImpl();

    @DisplayName("Get Order Success Scenario")
    @Test
    void test_When_Order_Success() {
        //Mocking
        Order order = getMockOrders();
        when(orderRepository.findById(anyInt()))
                .thenReturn(Optional.of(order));

        when(restTemplate.getForObject(
                "http://PRODUCT-SERVICE/product/" + order.getProductId(),
                ProductResponse.class
        )).thenReturn(getMockProductResponse());

        when(restTemplate.getForObject(
                "http://PAYMENT-SERVICE/payment/order/" + order.getId(),
                PaymentResponse.class
        )).thenReturn(getMockPaymentResponse());
        //Actual methods
        OrderResponse orderResponse = orderService.getOrderDetails(1);
        //Verification
        verify(orderRepository, times(1)).findById(anyInt());
        verify(restTemplate, times(1)).getForObject(
                "http://PRODUCT-SERVICE/product/" + order.getProductId(),
                ProductResponse.class
        );
        verify(restTemplate, times(1)).getForObject(
                "http://PAYMENT-SERVICE/payment/order/" + order.getId(),
                PaymentResponse.class
        );
        //Assert
        log.info("Order id {} ", order.getId());
        log.info("Order id {} ", orderResponse.getOrderId());
        assertNotNull(orderResponse);
        // assertEquals(Optional.of(order.getId()), Optional.of(orderResponse.getOrderId()));
    }

    @DisplayName("Get Orders = Failure Scenario")
    @Test
    void test_When_Get_Order_NOT_FOUND_then_Return_Not_found() {
        when(orderRepository.findById(anyInt()))
                .thenReturn(Optional.ofNullable(null));

        CustomException exception = assertThrows(CustomException.class,
                () -> orderService.getOrderDetails(1));
        assertEquals("Not_found", exception.getErrorCode());
        assertEquals(404, exception.getStatus());
        verify(orderRepository, times(1))
                .findById(anyInt());
    }

    @Test
    @DisplayName("Place Order - Success Scenario")
    void test_When_Place_Order_Success(){
        Order order = getMockOrders();
        OrderRequest orderRequest = getMockOrderRequest();
        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);
        when(productService.reduceQuantity(anyInt(), anyInt()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        int orderId = orderService.placeOrder(orderRequest);

        verify(orderRepository, times(2))
                .save(any());
        verify(productService, times(1))
                .reduceQuantity(anyInt(), anyLong());
        verify(paymentService, times(1))
                .doPayment(any(PaymentRequest.class));

        assertEquals(order.getId(), orderId);

    }

    @Test
    @DisplayName("Place Order - Payment Failed Scenario")
    void test_Place_Order_Payment_Fails_then_Order_Placed(){
        Order order = getMockOrders();
        OrderRequest orderRequest = getMockOrderRequest();
        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);
        when(productService.reduceQuantity(anyInt(), anyInt()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException());

        int orderId = orderService.placeOrder(orderRequest);

        verify(orderRepository, times(2))
                .save(any());
        verify(productService, times(1))
                .reduceQuantity(anyInt(), anyLong());
        verify(paymentService, times(1))
                .doPayment(any(PaymentRequest.class));
        assertEquals(order.getId(), orderId);

    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .productId(1)
                .quantity(10)
                .paymentMode(PaymentMode.CASH)
                .totalAmount(100)
                .build();
    }

    private PaymentResponse getMockPaymentResponse() {
        return PaymentResponse.builder()
                .paymentId(1)
                .paymentDate(LocalDateTime.now())
                .paymentMode(PaymentMode.CASH)
                .amount(200)
                .orderId(1)
                .status("ACCEPTED")
                .build();
    }

    private ProductResponse getMockProductResponse() {
        return ProductResponse.builder()
                .productId(1)
                .productName("IPhone")
                .price(100)
                .quantity(200)
                .build();
    }

    private Order getMockOrders() {
        return Order.builder()
                .orderStatus("PLACED")
                .orderDate(LocalDateTime.now())
                .id(1)
                .amount(100)
                .quantity(200)
                .productId(2).build();
    }

}