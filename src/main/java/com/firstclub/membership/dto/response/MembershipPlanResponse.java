package com.firstclub.membership.dto.response;

import com.firstclub.membership.enums.PlanType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class MembershipPlanResponse {
    private Long id;
    private PlanType planType;
    private String displayName;
    private BigDecimal price;
    private int durationDays;
    private String description;
    private boolean active;
    private BigDecimal pricePerDay;
}
