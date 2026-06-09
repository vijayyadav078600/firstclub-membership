package com.firstclub.membership.service.impl;

import com.firstclub.membership.dto.response.TierEvaluationResponse;
import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.entity.UserMembership;
import com.firstclub.membership.enums.CriteriaType;
import com.firstclub.membership.enums.MembershipStatus;
import com.firstclub.membership.enums.TierType;
import com.firstclub.membership.exception.MembershipException;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.MembershipTierRepository;
import com.firstclub.membership.repository.TierCriteriaRepository;
import com.firstclub.membership.repository.UserMembershipRepository;
import com.firstclub.membership.repository.UserRepository;
import com.firstclub.membership.service.TierEvaluationService;
import com.firstclub.membership.service.criteria.TierCriteriaEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TierEvaluationServiceImpl implements TierEvaluationService {

    private final UserRepository userRepository;
    private final MembershipTierRepository tierRepository;
    private final TierCriteriaRepository criteriaRepository;
    private final UserMembershipRepository membershipRepository;

    /**
     * All TierCriteriaEvaluator beans injected and indexed by CriteriaType at startup.
     * Adding a new CriteriaType only requires a new @Component — zero changes here.
     */
    private final Map<CriteriaType, TierCriteriaEvaluator> evaluatorMap;

    public TierEvaluationServiceImpl(
            UserRepository userRepository,
            MembershipTierRepository tierRepository,
            TierCriteriaRepository criteriaRepository,
            UserMembershipRepository membershipRepository,
            List<TierCriteriaEvaluator> evaluators) {
        this.userRepository = userRepository;
        this.tierRepository = tierRepository;
        this.criteriaRepository = criteriaRepository;
        this.membershipRepository = membershipRepository;
        this.evaluatorMap = evaluators.stream()
                .collect(Collectors.toMap(TierCriteriaEvaluator::getType, Function.identity()));
        log.info("TierEvaluationService initialized with {} criteria evaluators: {}",
                evaluatorMap.size(), evaluatorMap.keySet());
    }

    @Override
    @Transactional
    public TierEvaluationResponse evaluateAndUpdateTier(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        UserMembership membership = membershipRepository.findActiveByUserWithLock(user)
                .orElseThrow(() -> new MembershipException("No active membership found for user: " + userId));

        TierType previousTier = membership.getTier().getTierType();
        TierType qualifyingTier = computeHighestQualifyingTier(user);

        boolean changed = !previousTier.equals(qualifyingTier);
        if (changed) {
            MembershipTier newTier = tierRepository.findByTierType(qualifyingTier)
                    .orElseThrow(() -> new ResourceNotFoundException("Tier not found: " + qualifyingTier));
            membership.setTier(newTier);
            membershipRepository.save(membership);
            log.info("Tier auto-updated for user {}: {} → {}", userId, previousTier, qualifyingTier);
        }

        return TierEvaluationResponse.builder()
                .userId(userId)
                .previousTier(previousTier)
                .evaluatedTier(qualifyingTier)
                .tierChanged(changed)
                .reason(changed
                        ? "Tier updated from " + previousTier.getDisplayName() + " to " + qualifyingTier.getDisplayName()
                        : "No tier change — user remains at " + previousTier.getDisplayName())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TierType evaluateQualifyingTier(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        return computeHighestQualifyingTier(user);
    }

    /**
     * Iterates tiers from highest to lowest (Platinum → Gold → Silver).
     * Returns the first tier for which any of its criteria evaluate to true (OR logic).
     * Falls back to SILVER as the default entry tier.
     */
    private TierType computeHighestQualifyingTier(User user) {
        List<MembershipTier> tiers = tierRepository.findByActiveTrueOrderByTierLevelAsc();

        // Evaluate from highest tier down
        for (int i = tiers.size() - 1; i >= 0; i--) {
            MembershipTier tier = tiers.get(i);
            List<TierCriteria> criteriaList = criteriaRepository.findByTierAndActiveTrue(tier);

            if (criteriaList.isEmpty()) {
                log.debug("Tier {} has no criteria configured — skipping", tier.getTierType());
                continue;
            }

            boolean qualifies = criteriaList.stream().anyMatch(criteria -> {
                TierCriteriaEvaluator evaluator = evaluatorMap.get(criteria.getCriteriaType());
                if (evaluator == null) {
                    log.warn("No evaluator found for CriteriaType: {} — skipping", criteria.getCriteriaType());
                    return false;
                }
                return evaluator.evaluate(user, criteria);
            });

            if (qualifies) {
                log.debug("User {} qualifies for tier {}", user.getId(), tier.getTierType());
                return tier.getTierType();
            }
        }

        return TierType.SILVER;
    }
}
