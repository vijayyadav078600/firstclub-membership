package com.firstclub.membership.service.impl;

import com.firstclub.membership.dto.response.MembershipPlanResponse;
import com.firstclub.membership.entity.MembershipPlan;
import com.firstclub.membership.enums.PlanType;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.MembershipPlanRepository;
import com.firstclub.membership.service.MembershipPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MembershipPlanServiceImpl implements MembershipPlanService {

    private final MembershipPlanRepository planRepository;
    private final MembershipMapper mapper;

    @Override
    public List<MembershipPlanResponse> getAllActivePlans() {
        return planRepository.findByActiveTrue().stream()
                .map(mapper::toPlanResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MembershipPlanResponse getPlanById(Long id) {
        MembershipPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MembershipPlan", id));
        return mapper.toPlanResponse(plan);
    }

    @Override
    public MembershipPlanResponse getPlanByType(PlanType planType) {
        MembershipPlan plan = planRepository.findByPlanType(planType)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found for type: " + planType));
        return mapper.toPlanResponse(plan);
    }
}
