package com.firstclub.membership.service;

import com.firstclub.membership.dto.response.MembershipPlanResponse;
import com.firstclub.membership.enums.PlanType;

import java.util.List;

public interface MembershipPlanService {
    List<MembershipPlanResponse> getAllActivePlans();
    MembershipPlanResponse getPlanById(Long id);
    MembershipPlanResponse getPlanByType(PlanType planType);
}
