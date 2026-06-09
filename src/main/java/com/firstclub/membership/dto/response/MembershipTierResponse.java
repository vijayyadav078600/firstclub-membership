package com.firstclub.membership.dto.response;

import com.firstclub.membership.enums.TierType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MembershipTierResponse {
    private Long id;
    private TierType tierType;
    private String displayName;
    private int tierLevel;
    private String description;
    private boolean active;
    private List<TierBenefitResponse> benefits;
    private List<TierCriteriaResponse> criteria;
}
