package com.firstclub.membership.repository;

import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.TierCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TierCriteriaRepository extends JpaRepository<TierCriteria, Long> {
    List<TierCriteria> findByTierAndActiveTrue(MembershipTier tier);
}
