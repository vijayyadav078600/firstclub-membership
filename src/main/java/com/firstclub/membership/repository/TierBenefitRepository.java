package com.firstclub.membership.repository;

import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.TierBenefit;
import com.firstclub.membership.enums.BenefitType;
import com.firstclub.membership.enums.TierType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TierBenefitRepository extends JpaRepository<TierBenefit, Long> {
    List<TierBenefit> findByTierAndActiveTrue(MembershipTier tier);

    @Query("SELECT b FROM TierBenefit b WHERE b.tier.tierType = :tierType AND b.active = true")
    List<TierBenefit> findActiveByTierType(TierType tierType);

    Optional<TierBenefit> findByTierAndBenefitType(MembershipTier tier, BenefitType benefitType);
}
