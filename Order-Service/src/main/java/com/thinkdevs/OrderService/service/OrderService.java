package com.thinkdevs.OrderService.service;

import com.thinkdevs.OrderService.model.OrderRequest;
import com.thinkdevs.OrderService.model.OrderResponse;

public interface OrderService {
     Integer placeOrder(OrderRequest request);


    OrderResponse getOrderDetails(Integer orderId);
}
