package com.firstclub.membership.repository;

import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.enums.TierType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipTierRepository extends JpaRepository<MembershipTier, Long> {
    Optional<MembershipTier> findByTierType(TierType tierType);
    List<MembershipTier> findByActiveTrueOrderByTierLevelAsc();

    /**
     * Load tiers with their benefits eagerly (one JOIN FETCH is safe).
     * Criteria are loaded lazily within the calling @Transactional service.
     * Splitting into two join-fetches on List-type bags causes MultipleBagFetchException.
     */
    @Query("SELECT DISTINCT t FROM MembershipTier t LEFT JOIN FETCH t.benefits WHERE t.active = true ORDER BY t.tierLevel ASC")
    List<MembershipTier> findAllActiveWithBenefitsAndCriteria();

    @Query("SELECT DISTINCT t FROM MembershipTier t LEFT JOIN FETCH t.benefits WHERE t.tierType = :tierType")
    Optional<MembershipTier> findByTierTypeWithDetails(TierType tierType);
}
