package com.firstclub.membership.controller;

import com.firstclub.membership.dto.request.CreateOrderRequest;
import com.firstclub.membership.dto.response.ApiResponse;
import com.firstclub.membership.dto.response.OrderResponse;
import com.firstclub.membership.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Place orders — membership benefits are applied automatically")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Place an order — member discounts and free delivery are auto-applied")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order placed successfully", orderService.createOrder(request)));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all orders for a user")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getUserOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getUserOrders(userId)));
    }
}
