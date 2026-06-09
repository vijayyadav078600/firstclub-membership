package com.firstclub.membership.controller;

import com.firstclub.membership.dto.response.ApiResponse;
import com.firstclub.membership.dto.response.MembershipPlanResponse;
import com.firstclub.membership.enums.PlanType;
import com.firstclub.membership.service.MembershipPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
@Tag(name = "Membership Plans", description = "Browse available membership plans")
public class MembershipPlanController {

    private final MembershipPlanService planService;

    @GetMapping
    @Operation(summary = "Get all active membership plans")
    public ResponseEntity<ApiResponse<List<MembershipPlanResponse>>> getAllPlans() {
        return ResponseEntity.ok(ApiResponse.success(planService.getAllActivePlans()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get membership plan by ID")
    public ResponseEntity<ApiResponse<MembershipPlanResponse>> getPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(planService.getPlanById(id)));
    }

    @GetMapping("/type/{planType}")
    @Operation(summary = "Get membership plan by type (MONTHLY, QUARTERLY, YEARLY)")
    public ResponseEntity<ApiResponse<MembershipPlanResponse>> getPlanByType(@PathVariable PlanType planType) {
        return ResponseEntity.ok(ApiResponse.success(planService.getPlanByType(planType)));
    }
}
