package com.firstclub.membership.service.impl;

import com.firstclub.membership.dto.request.CreateOrderRequest;
import com.firstclub.membership.dto.response.OrderResponse;
import com.firstclub.membership.entity.*;
import com.firstclub.membership.enums.BenefitType;
import com.firstclub.membership.enums.MembershipStatus;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.*;
import com.firstclub.membership.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final UserMembershipRepository membershipRepository;
    private final TierBenefitRepository benefitRepository;
    private final MembershipMapper mapper;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));

        BigDecimal orderAmount = request.getOrderAmount();
        BigDecimal discount = BigDecimal.ZERO;
        boolean freeDelivery = false;

        Optional<UserMembership> activeMembership = membershipRepository.findActiveByUser(user);

        if (activeMembership.isPresent() && activeMembership.get().isActive()) {
            UserMembership membership = activeMembership.get();
            MembershipTier tier = membership.getTier();
            List<TierBenefit> benefits = benefitRepository.findByTierAndActiveTrue(tier);

            for (TierBenefit benefit : benefits) {
                switch (benefit.getBenefitType()) {
                    case DISCOUNT_PERCENTAGE -> {
                        BigDecimal discountRate = benefit.getValue().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                        discount = orderAmount.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
                        log.debug("Applied {}% discount = {} for user {}", benefit.getValue(), discount, user.getId());
                    }
                    case FREE_DELIVERY -> {
                        // Free delivery if order meets minimum threshold (0 = always free)
                        if (benefit.getValue().compareTo(BigDecimal.ZERO) == 0
                                || orderAmount.compareTo(benefit.getValue()) >= 0) {
                            freeDelivery = true;
                        }
                    }
                    default -> { /* Other benefits are informational, not applied at order time */ }
                }
            }
        }

        BigDecimal finalAmount = orderAmount.subtract(discount).max(BigDecimal.ZERO);

        Order order = Order.builder()
                .user(user)
                .orderAmount(orderAmount)
                .discountApplied(discount)
                .finalAmount(finalAmount)
                .freeDeliveryApplied(freeDelivery)
                .notes(request.getNotes())
                .build();

        Order saved = orderRepository.save(order);
        log.info("Order {} created for user {}: ₹{} - ₹{} discount = ₹{} final",
                saved.getId(), user.getId(), orderAmount, discount, finalAmount);
        return mapper.toOrderResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        return orderRepository.findByUser(user).stream()
                .map(mapper::toOrderResponse)
                .collect(Collectors.toList());
    }
}
