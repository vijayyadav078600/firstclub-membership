package com.firstclub.membership.service;

import com.firstclub.membership.dto.response.MembershipTierResponse;
import com.firstclub.membership.enums.TierType;

import java.util.List;

public interface MembershipTierService {
    List<MembershipTierResponse> getAllActiveTiers();
    MembershipTierResponse getTierById(Long id);
    MembershipTierResponse getTierByType(TierType tierType);
}
