package com.firstclub.membership.dto.response;

import com.firstclub.membership.enums.BenefitType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class TierBenefitResponse {
    private Long id;
    private BenefitType benefitType;
    private String benefitDisplayName;
    private BigDecimal value;
    private String description;
    private String metadata;
}
