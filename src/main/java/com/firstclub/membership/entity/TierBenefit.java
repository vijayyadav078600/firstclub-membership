package com.firstclub.membership.entity;

import com.firstclub.membership.enums.BenefitType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a single configurable benefit attached to a membership tier.
 * The `value` field holds numeric data (e.g., discount %, min order amount).
 * The `metadata` field holds any extra free-form configuration as a string.
 */
@Entity
@Table(name = "tier_benefits")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TierBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @Enumerated(EnumType.STRING)
    @Column(name = "benefit_type", nullable = false)
    private BenefitType benefitType;

    /**
     * Numeric value for the benefit:
     * - FREE_DELIVERY: minimum order amount (0 = always free)
     * - DISCOUNT_PERCENTAGE: discount %
     * - others: unused (set to 0)
     */
    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal value = BigDecimal.ZERO;

    @Column(nullable = false)
    private String description;

    /** Extensible JSON/text blob for future metadata (e.g., category restrictions) */
    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
