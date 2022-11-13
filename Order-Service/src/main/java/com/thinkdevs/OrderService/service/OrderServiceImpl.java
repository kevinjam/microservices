package com.thinkdevs.OrderService.service;

import com.thinkdevs.OrderService.entity.Order;
import com.thinkdevs.OrderService.exception.CustomException;
import com.thinkdevs.OrderService.external.client.PaymentService;
import com.thinkdevs.OrderService.external.client.ProductService;
import com.thinkdevs.OrderService.external.request.PaymentRequest;
import com.thinkdevs.OrderService.external.response.PaymentResponse;
import com.thinkdevs.OrderService.model.OrderRequest;
import com.thinkdevs.OrderService.model.OrderResponse;
import com.thinkdevs.OrderService.model.ProductResponse;
import com.thinkdevs.OrderService.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired

    private ProductService productService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Integer placeOrder(OrderRequest request) {
        log.info("Placing Order Request: {}", request);

        productService.reduceQuantity(request.getProductId(), request.getQuantity());
        Order order = Order.builder()
                .amount(request.getTotalAmount())
                .orderStatus("CREATED")
                .productId(request.getProductId())
                .orderDate(LocalDateTime.now())
                .quantity(request.getQuantity())
                .active(true)
                .build();
        order = orderRepository.save(order);
        log.info("Creating Order with Status CREATED");

        log.info(("Calling payment service"));
        PaymentRequest paymentRequest
                = PaymentRequest.builder()
                .orderId(order.getId())
                .paymentMode(request.getPaymentMode())
                .amount(request.getTotalAmount())
                .build();

        String orderStatus = null;
        try {
            paymentService.doPayment(paymentRequest);
            log.info("Payment successful changing the order status");
            orderStatus = "PLACED";
        } catch (Exception e) {
            log.error("Error occurred in payment. Changing order status to PAYMENT_FAILED");
            orderStatus = "PAYMENT_FAILED";
        }
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);

        log.info("Order Places successfully with Order Id: {}", order.getId());

        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(Integer orderId) {
        log.info("Get order details for Order Id : {}", orderId);
        Order order
                = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found for the order id : " + orderId,
                        "Not_found", 404)
                );

        log.info("Invoking Product service to fetch the product for id: {}", order.getProductId());
        ProductResponse productResponse
                = restTemplate.getForObject(
                "http://PRODUCT-SERVICE/product/" + order.getProductId(),
                ProductResponse.class);

        log.info("Getting payment information from payment service");
        PaymentResponse paymentResponse
                = restTemplate.getForObject("http://PAYMENT-SERVICE/payment/order/" + order.getId(),
                PaymentResponse.class);

        OrderResponse.PaymentDetails paymentDetails
                = OrderResponse.PaymentDetails.builder()
                .paymentId(paymentResponse.getPaymentId())
                .paymentDate(paymentResponse.getPaymentDate())
                .paymentStatus(paymentResponse.getStatus())
                .paymentMode(paymentResponse.getPaymentMode())
                .build();


        OrderResponse.ProductDetails productDetails
                = OrderResponse.ProductDetails.builder()
                .productName(productResponse.getProductName())
                .productId(productResponse.getProductId())
                .quantity(productResponse.getQuantity())
                .price(productResponse.getPrice())
                .build();

        OrderResponse orderResponse
                = OrderResponse.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();

        log.info("Invoking Product service  to fetch");
        return orderResponse;
    }


}
