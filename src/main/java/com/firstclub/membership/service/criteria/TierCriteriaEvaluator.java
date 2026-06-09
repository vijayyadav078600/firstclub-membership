package com.firstclub.membership.service.criteria;

import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.CriteriaType;

/**
 * Strategy interface for evaluating whether a user satisfies a specific tier criterion.
 *
 * Each implementation handles exactly one CriteriaType, registered as a Spring bean.
 * TierEvaluationService discovers all evaluators via dependency injection and
 * dispatches to the right one at runtime — adding a new criterion type only
 * requires writing a new class; no existing code changes.
 */
public interface TierCriteriaEvaluator {

    /**
     * @return the CriteriaType this evaluator handles
     */
    CriteriaType getType();

    /**
     * @param user     the user being evaluated
     * @param criteria the configured threshold to check against
     * @return true if the user meets the criterion
     */
    boolean evaluate(User user, TierCriteria criteria);
}
