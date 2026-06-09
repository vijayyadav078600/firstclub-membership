package com.firstclub.membership.service.criteria;

import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.CohortType;
import com.firstclub.membership.enums.CriteriaType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Evaluates tier eligibility based on the user's cohort level.
 * A user qualifies if their cohort level >= the required cohort level.
 * E.g. if criteria requires PREMIUM (level 2), a VIP (level 3) user also qualifies.
 */
@Component
@Slf4j
public class CohortCriteriaEvaluator implements TierCriteriaEvaluator {

    @Override
    public CriteriaType getType() {
        return CriteriaType.COHORT_MEMBERSHIP;
    }

    @Override
    public boolean evaluate(User user, TierCriteria criteria) {
        CohortType userCohort = user.getCohort();
        CohortType requiredCohort = criteria.getRequiredCohort();

        if (requiredCohort == null) {
            log.warn("COHORT_MEMBERSHIP criteria {} has no requiredCohort set — skipping", criteria.getId());
            return false;
        }

        boolean qualifies = userCohort.getLevel() >= requiredCohort.getLevel();
        log.debug("COHORT eval for user {}: cohort {} (level {}) vs required {} (level {}) → {}",
                user.getId(), userCohort, userCohort.getLevel(),
                requiredCohort, requiredCohort.getLevel(), qualifies);
        return qualifies;
    }
}
