package com.firstclub.membership.dto.request;

import com.firstclub.membership.enums.PlanType;
import com.firstclub.membership.enums.TierType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscribeRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Plan type is required")
    private PlanType planType;

    @NotNull(message = "Tier type is required")
    private TierType tierType;

    private boolean autoRenew = true;
}
