package com.firstclub.membership.service.criteria;

import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.CriteriaType;
import com.firstclub.membership.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderValueMonthlyCriteriaEvaluator implements TierCriteriaEvaluator {

    private final OrderRepository orderRepository;

    @Override
    public CriteriaType getType() {
        return CriteriaType.ORDER_VALUE_MONTHLY;
    }

    @Override
    public boolean evaluate(User user, TierCriteria criteria) {
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime from = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime to = currentMonth.atEndOfMonth().atTime(23, 59, 59);

        BigDecimal monthlySpend = orderRepository.sumCompletedOrderAmountByUserInPeriod(user, from, to);
        boolean qualifies = monthlySpend.compareTo(criteria.getThresholdValue()) >= 0;
        log.debug("ORDER_VALUE_MONTHLY eval for user {}: ₹{} this month vs threshold ₹{} → {}",
                user.getId(), monthlySpend, criteria.getThresholdValue(), qualifies);
        return qualifies;
    }
}
