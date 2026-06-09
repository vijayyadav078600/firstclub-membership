package com.firstclub.membership.service.impl;

import com.firstclub.membership.dto.response.*;
import com.firstclub.membership.entity.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MembershipMapper {

    public MembershipPlanResponse toPlanResponse(MembershipPlan plan) {
        BigDecimal pricePerDay = plan.getDurationDays() > 0
                ? plan.getPrice().divide(BigDecimal.valueOf(plan.getDurationDays()), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return MembershipPlanResponse.builder()
                .id(plan.getId())
                .planType(plan.getPlanType())
                .displayName(plan.getPlanType().getDisplayName())
                .price(plan.getPrice())
                .durationDays(plan.getDurationDays())
                .description(plan.getDescription())
                .active(plan.isActive())
                .pricePerDay(pricePerDay)
                .build();
    }

    public TierBenefitResponse toBenefitResponse(TierBenefit benefit) {
        return TierBenefitResponse.builder()
                .id(benefit.getId())
                .benefitType(benefit.getBenefitType())
                .benefitDisplayName(benefit.getBenefitType().getDisplayName())
                .value(benefit.getValue())
                .description(benefit.getDescription())
                .metadata(benefit.getMetadata())
                .build();
    }

    public TierCriteriaResponse toCriteriaResponse(TierCriteria criteria) {
        return TierCriteriaResponse.builder()
                .id(criteria.getId())
                .criteriaType(criteria.getCriteriaType())
                .criteriaDisplayName(criteria.getCriteriaType().getDisplayName())
                .thresholdValue(criteria.getThresholdValue())
                .requiredCohort(criteria.getRequiredCohort())
                .description(criteria.getCriteriaType().getDescription())
                .build();
    }

    public MembershipTierResponse toTierResponse(MembershipTier tier) {
        List<TierBenefitResponse> benefits = tier.getBenefits().stream()
                .filter(TierBenefit::isActive)
                .map(this::toBenefitResponse)
                .collect(Collectors.toList());

        List<TierCriteriaResponse> criteria = tier.getCriteria().stream()
                .filter(TierCriteria::isActive)
                .map(this::toCriteriaResponse)
                .collect(Collectors.toList());

        return MembershipTierResponse.builder()
                .id(tier.getId())
                .tierType(tier.getTierType())
                .displayName(tier.getTierType().getDisplayName())
                .tierLevel(tier.getTierLevel())
                .description(tier.getDescription())
                .active(tier.isActive())
                .benefits(benefits)
                .criteria(criteria)
                .build();
    }

    public UserMembershipResponse toMembershipResponse(UserMembership membership) {
        long daysRemaining = Math.max(0, ChronoUnit.DAYS.between(LocalDateTime.now(), membership.getEndDate()));

        List<TierBenefitResponse> benefits = membership.getTier().getBenefits().stream()
                .filter(TierBenefit::isActive)
                .map(this::toBenefitResponse)
                .collect(Collectors.toList());

        return UserMembershipResponse.builder()
                .membershipId(membership.getId())
                .userId(membership.getUser().getId())
                .userName(membership.getUser().getName())
                .planType(membership.getPlan().getPlanType())
                .planDisplayName(membership.getPlan().getPlanType().getDisplayName())
                .tierType(membership.getTier().getTierType())
                .tierDisplayName(membership.getTier().getTierType().getDisplayName())
                .tierLevel(membership.getTier().getTierLevel())
                .status(membership.getStatus())
                .startDate(membership.getStartDate())
                .endDate(membership.getEndDate())
                .daysRemaining(daysRemaining)
                .autoRenew(membership.isAutoRenew())
                .active(membership.isActive())
                .activeBenefits(benefits)
                .build();
    }

    public OrderResponse toOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .userName(order.getUser().getName())
                .orderAmount(order.getOrderAmount())
                .discountApplied(order.getDiscountApplied())
                .finalAmount(order.getFinalAmount())
                .status(order.getStatus())
                .freeDeliveryApplied(order.isFreeDeliveryApplied())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
