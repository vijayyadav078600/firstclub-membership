package com.firstclub.membership.config;

import com.firstclub.membership.entity.UserMembership;
import com.firstclub.membership.enums.MembershipStatus;
import com.firstclub.membership.repository.UserMembershipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Periodic background job that transitions ACTIVE memberships past their end date
 * into EXPIRED status. Runs every hour.
 *
 * In a production system this would also trigger renewal notifications,
 * auto-renewal charges, and tier re-evaluation.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MembershipExpiryScheduler {

    private final UserMembershipRepository membershipRepository;

    @Scheduled(fixedRateString = "PT1H")
    @Transactional
    public void expireOverdueMemberships() {
        List<UserMembership> expired = membershipRepository
                .findByStatusAndEndDateBefore(MembershipStatus.ACTIVE, LocalDateTime.now());

        if (expired.isEmpty()) return;

        expired.forEach(m -> {
            m.setStatus(MembershipStatus.EXPIRED);
            log.info("Membership {} for user {} marked EXPIRED (end date: {})",
                    m.getId(), m.getUser().getId(), m.getEndDate());
        });

        membershipRepository.saveAll(expired);
        log.info("Expiry sweep: {} memberships transitioned to EXPIRED", expired.size());
    }
}
