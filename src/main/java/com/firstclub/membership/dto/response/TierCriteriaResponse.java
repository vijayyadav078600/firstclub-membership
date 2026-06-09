package com.firstclub.membership.dto.response;

import com.firstclub.membership.enums.CohortType;
import com.firstclub.membership.enums.CriteriaType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class TierCriteriaResponse {
    private Long id;
    private CriteriaType criteriaType;
    private String criteriaDisplayName;
    private BigDecimal thresholdValue;
    private CohortType requiredCohort;
    private String description;
}
