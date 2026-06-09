package com.firstclub.membership.service.impl;

import com.firstclub.membership.dto.response.MembershipTierResponse;
import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.enums.TierType;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.MembershipTierRepository;
import com.firstclub.membership.service.MembershipTierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MembershipTierServiceImpl implements MembershipTierService {

    private final MembershipTierRepository tierRepository;
    private final MembershipMapper mapper;

    @Override
    public List<MembershipTierResponse> getAllActiveTiers() {
        return tierRepository.findAllActiveWithBenefitsAndCriteria().stream()
                .map(mapper::toTierResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MembershipTierResponse getTierById(Long id) {
        MembershipTier tier = tierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MembershipTier", id));
        return mapper.toTierResponse(tier);
    }

    @Override
    public MembershipTierResponse getTierByType(TierType tierType) {
        MembershipTier tier = tierRepository.findByTierTypeWithDetails(tierType)
                .orElseThrow(() -> new ResourceNotFoundException("Tier not found for type: " + tierType));
        return mapper.toTierResponse(tier);
    }
}
