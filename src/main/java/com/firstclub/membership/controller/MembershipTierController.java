package com.firstclub.membership.controller;

import com.firstclub.membership.dto.response.ApiResponse;
import com.firstclub.membership.dto.response.MembershipTierResponse;
import com.firstclub.membership.enums.TierType;
import com.firstclub.membership.service.MembershipTierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tiers")
@RequiredArgsConstructor
@Tag(name = "Membership Tiers", description = "Browse tiers with benefits and qualification criteria")
public class MembershipTierController {

    private final MembershipTierService tierService;

    @GetMapping
    @Operation(summary = "Get all active membership tiers with benefits and criteria")
    public ResponseEntity<ApiResponse<List<MembershipTierResponse>>> getAllTiers() {
        return ResponseEntity.ok(ApiResponse.success(tierService.getAllActiveTiers()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get membership tier by ID")
    public ResponseEntity<ApiResponse<MembershipTierResponse>> getTierById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(tierService.getTierById(id)));
    }

    @GetMapping("/type/{tierType}")
    @Operation(summary = "Get membership tier by type (SILVER, GOLD, PLATINUM)")
    public ResponseEntity<ApiResponse<MembershipTierResponse>> getTierByType(@PathVariable TierType tierType) {
        return ResponseEntity.ok(ApiResponse.success(tierService.getTierByType(tierType)));
    }
}
