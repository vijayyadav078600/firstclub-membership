package com.firstclub.membership.service.impl;

import com.firstclub.membership.dto.request.ChangeTierRequest;
import com.firstclub.membership.dto.request.SubscribeRequest;
import com.firstclub.membership.dto.response.UserMembershipResponse;
import com.firstclub.membership.entity.MembershipPlan;
import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.entity.UserMembership;
import com.firstclub.membership.enums.MembershipStatus;
import com.firstclub.membership.exception.MembershipException;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.MembershipPlanRepository;
import com.firstclub.membership.repository.MembershipTierRepository;
import com.firstclub.membership.repository.UserMembershipRepository;
import com.firstclub.membership.repository.UserRepository;
import com.firstclub.membership.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final UserRepository userRepository;
    private final MembershipPlanRepository planRepository;
    private final MembershipTierRepository tierRepository;
    private final UserMembershipRepository membershipRepository;
    private final MembershipMapper mapper;

    @Override
    @Transactional
    public UserMembershipResponse subscribe(SubscribeRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));

        if (membershipRepository.existsByUserAndStatus(user, MembershipStatus.ACTIVE)) {
            throw new MembershipException("User already has an active membership. Cancel existing membership before subscribing.");
        }

        MembershipPlan plan = planRepository.findByPlanType(request.getPlanType())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found: " + request.getPlanType()));

        if (!plan.isActive()) {
            throw new MembershipException("The selected plan is currently unavailable.");
        }

        MembershipTier tier = tierRepository.findByTierType(request.getTierType())
                .orElseThrow(() -> new ResourceNotFoundException("Tier not found: " + request.getTierType()));

        LocalDateTime now = LocalDateTime.now();
        UserMembership membership = UserMembership.builder()
                .user(user)
                .plan(plan)
                .tier(tier)
                .startDate(now)
                .endDate(now.plusDays(plan.getDurationDays()))
                .status(MembershipStatus.ACTIVE)
                .autoRenew(request.isAutoRenew())
                .build();

        UserMembership saved = membershipRepository.save(membership);
        log.info("User {} subscribed to {} plan with {} tier", user.getId(), plan.getPlanType(), tier.getTierType());
        return mapper.toMembershipResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserMembershipResponse getMembership(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        UserMembership membership = membershipRepository.findActiveByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("No active membership found for user: " + userId));

        if (membership.isExpired()) {
            throw new MembershipException("Membership has expired. Please renew.");
        }

        return mapper.toMembershipResponse(membership);
    }

    /**
     * Tier change uses SERIALIZABLE isolation + pessimistic write lock to guarantee
     * that concurrent upgrade/downgrade requests don't interleave and produce an
     * inconsistent tier state. The @Version field on UserMembership provides a second
     * safety net at the ORM level.
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserMembershipResponse upgradeTier(Long userId, ChangeTierRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        UserMembership membership = membershipRepository.findActiveByUserWithLock(user)
                .orElseThrow(() -> new MembershipException("No active membership to upgrade."));

        if (membership.isExpired()) {
            throw new MembershipException("Cannot upgrade an expired membership.");
        }

        MembershipTier currentTier = membership.getTier();
        if (!request.getTargetTier().isHigherThan(currentTier.getTierType())) {
            throw new MembershipException(
                    "Target tier " + request.getTargetTier() + " is not higher than current tier " + currentTier.getTierType());
        }

        MembershipTier newTier = tierRepository.findByTierType(request.getTargetTier())
                .orElseThrow(() -> new ResourceNotFoundException("Tier not found: " + request.getTargetTier()));

        membership.setTier(newTier);
        UserMembership updated = membershipRepository.save(membership);
        log.info("User {} upgraded from {} to {}", userId, currentTier.getTierType(), newTier.getTierType());
        return mapper.toMembershipResponse(updated);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserMembershipResponse downgradeTier(Long userId, ChangeTierRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        UserMembership membership = membershipRepository.findActiveByUserWithLock(user)
                .orElseThrow(() -> new MembershipException("No active membership to downgrade."));

        if (membership.isExpired()) {
            throw new MembershipException("Cannot downgrade an expired membership.");
        }

        MembershipTier currentTier = membership.getTier();
        if (!request.getTargetTier().isLowerThan(currentTier.getTierType())) {
            throw new MembershipException(
                    "Target tier " + request.getTargetTier() + " is not lower than current tier " + currentTier.getTierType());
        }

        MembershipTier newTier = tierRepository.findByTierType(request.getTargetTier())
                .orElseThrow(() -> new ResourceNotFoundException("Tier not found: " + request.getTargetTier()));

        membership.setTier(newTier);
        UserMembership updated = membershipRepository.save(membership);
        log.info("User {} downgraded from {} to {}", userId, currentTier.getTierType(), newTier.getTierType());
        return mapper.toMembershipResponse(updated);
    }

    @Override
    @Transactional
    public UserMembershipResponse cancelMembership(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        UserMembership membership = membershipRepository.findActiveByUserWithLock(user)
                .orElseThrow(() -> new MembershipException("No active membership to cancel."));

        membership.setStatus(MembershipStatus.CANCELLED);
        membership.setCancelledAt(LocalDateTime.now());
        membership.setAutoRenew(false);

        UserMembership saved = membershipRepository.save(membership);
        log.info("User {} cancelled membership {}", userId, membership.getId());
        return mapper.toMembershipResponse(saved);
    }
}
