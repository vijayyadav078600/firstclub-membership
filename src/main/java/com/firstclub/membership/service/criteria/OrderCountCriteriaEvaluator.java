package com.firstclub.membership.service.criteria;

import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.CriteriaType;
import com.firstclub.membership.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCountCriteriaEvaluator implements TierCriteriaEvaluator {

    private final OrderRepository orderRepository;

    @Override
    public CriteriaType getType() {
        return CriteriaType.ORDER_COUNT;
    }

    @Override
    public boolean evaluate(User user, TierCriteria criteria) {
        long completedOrders = orderRepository.countCompletedOrdersByUser(user);
        long threshold = criteria.getThresholdValue().longValue();
        boolean qualifies = completedOrders >= threshold;
        log.debug("ORDER_COUNT eval for user {}: {} completed orders vs threshold {} → {}",
                user.getId(), completedOrders, threshold, qualifies);
        return qualifies;
    }
}
