package com.firstclub.membership.service;

import com.firstclub.membership.dto.request.ChangeTierRequest;
import com.firstclub.membership.dto.request.SubscribeRequest;
import com.firstclub.membership.dto.response.UserMembershipResponse;

public interface SubscriptionService {

    /** Create a new active subscription for the user. Fails if one already exists. */
    UserMembershipResponse subscribe(SubscribeRequest request);

    /** Retrieve the user's current active membership. */
    UserMembershipResponse getMembership(Long userId);

    /**
     * Upgrade the user's tier (e.g. Silver → Gold → Platinum).
     * Thread-safe via optimistic locking.
     */
    UserMembershipResponse upgradeTier(Long userId, ChangeTierRequest request);

    /**
     * Downgrade the user's tier (e.g. Platinum → Gold → Silver).
     * Thread-safe via optimistic locking.
     */
    UserMembershipResponse downgradeTier(Long userId, ChangeTierRequest request);

    /** Cancel an active membership. */
    UserMembershipResponse cancelMembership(Long userId);
}
