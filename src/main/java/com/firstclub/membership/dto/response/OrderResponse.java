package com.firstclub.membership.dto.response;

import com.firstclub.membership.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private String userName;
    private BigDecimal orderAmount;
    private BigDecimal discountApplied;
    private BigDecimal finalAmount;
    private OrderStatus status;
    private boolean freeDeliveryApplied;
    private String notes;
    private LocalDateTime createdAt;
}
