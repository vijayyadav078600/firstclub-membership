package com.firstclub.membership.service;

import com.firstclub.membership.dto.response.TierEvaluationResponse;
import com.firstclub.membership.enums.TierType;

public interface TierEvaluationService {

    /**
     * Evaluate which tier a user qualifies for based on configured criteria,
     * then apply it to their active membership if it changed.
     */
    TierEvaluationResponse evaluateAndUpdateTier(Long userId);

    /** Pure evaluation — returns the highest qualifying tier without persisting changes. */
    TierType evaluateQualifyingTier(Long userId);
}
