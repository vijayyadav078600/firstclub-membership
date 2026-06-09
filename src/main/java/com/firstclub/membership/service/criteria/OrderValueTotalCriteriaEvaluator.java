package com.firstclub.membership.service.criteria;

import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.CriteriaType;
import com.firstclub.membership.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderValueTotalCriteriaEvaluator implements TierCriteriaEvaluator {

    private final OrderRepository orderRepository;

    @Override
    public CriteriaType getType() {
        return CriteriaType.ORDER_VALUE_TOTAL;
    }

    @Override
    public boolean evaluate(User user, TierCriteria criteria) {
        BigDecimal totalSpend = orderRepository.sumTotalCompletedOrderAmountByUser(user);
        boolean qualifies = totalSpend.compareTo(criteria.getThresholdValue()) >= 0;
        log.debug("ORDER_VALUE_TOTAL eval for user {}: ₹{} lifetime vs threshold ₹{} → {}",
                user.getId(), totalSpend, criteria.getThresholdValue(), qualifies);
        return qualifies;
    }
}
