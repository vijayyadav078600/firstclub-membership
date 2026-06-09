package com.firstclub.membership.entity;

import com.firstclub.membership.enums.CohortType;
import com.firstclub.membership.enums.CriteriaType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Defines the condition(s) under which a user qualifies for a given tier.
 * Multiple criteria per tier are evaluated with OR logic — satisfying any one
 * is sufficient. This makes the system fully configurable without code changes.
 */
@Entity
@Table(name = "tier_criteria")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TierCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @Enumerated(EnumType.STRING)
    @Column(name = "criteria_type", nullable = false)
    private CriteriaType criteriaType;

    /**
     * Threshold value — interpreted differently per criteriaType:
     * - ORDER_COUNT: minimum number of completed orders
     * - ORDER_VALUE_MONTHLY / ORDER_VALUE_TOTAL: minimum spend in currency units
     * - COHORT_MEMBERSHIP: minimum cohort level (maps to CohortType.level)
     */
    @Column(name = "threshold_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal thresholdValue;

    /**
     * For COHORT_MEMBERSHIP criteria: the specific cohort required.
     * Nullable for non-cohort criteria types.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "required_cohort")
    private CohortType requiredCohort;

    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
