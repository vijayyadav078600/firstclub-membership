package com.firstclub.membership.dto.response;

import com.firstclub.membership.enums.TierType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TierEvaluationResponse {
    private Long userId;
    private TierType previousTier;
    private TierType evaluatedTier;
    private boolean tierChanged;
    private String reason;
}
