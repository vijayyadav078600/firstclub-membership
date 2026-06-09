package com.firstclub.membership.service;

import com.firstclub.membership.dto.request.CreateOrderRequest;
import com.firstclub.membership.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    List<OrderResponse> getUserOrders(Long userId);
}
