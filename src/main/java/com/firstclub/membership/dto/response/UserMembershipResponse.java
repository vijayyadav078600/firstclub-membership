package com.firstclub.membership.dto.response;

import com.firstclub.membership.enums.MembershipStatus;
import com.firstclub.membership.enums.PlanType;
import com.firstclub.membership.enums.TierType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UserMembershipResponse {
    private Long membershipId;
    private Long userId;
    private String userName;
    private PlanType planType;
    private String planDisplayName;
    private TierType tierType;
    private String tierDisplayName;
    private int tierLevel;
    private MembershipStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private long daysRemaining;
    private boolean autoRenew;
    private boolean active;
    private List<TierBenefitResponse> activeBenefits;
}
