package com.firstclub.membership.entity;

import com.firstclub.membership.enums.MembershipStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Core aggregate for a user's active membership.
 * The @Version field enables optimistic locking — concurrent tier changes
 * (e.g., user-initiated upgrade racing against an automated tier evaluation)
 * will result in an OptimisticLockException rather than a lost update.
 */
@Entity
@Table(name = "user_memberships",
        indexes = {
                @Index(name = "idx_user_membership_user_id", columnList = "user_id"),
                @Index(name = "idx_user_membership_status", columnList = "status")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private MembershipPlan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MembershipStatus status = MembershipStatus.ACTIVE;

    @Column(name = "auto_renew")
    @Builder.Default
    private boolean autoRenew = true;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    /** Optimistic lock version — prevents concurrent modification races */
    @Version
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public boolean isActive() {
        return MembershipStatus.ACTIVE.equals(this.status)
                && LocalDateTime.now().isBefore(this.endDate);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.endDate)
                && MembershipStatus.ACTIVE.equals(this.status);
    }
}
