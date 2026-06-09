package com.firstclub.membership.controller;

import com.firstclub.membership.dto.request.ChangeTierRequest;
import com.firstclub.membership.dto.request.SubscribeRequest;
import com.firstclub.membership.dto.response.ApiResponse;
import com.firstclub.membership.dto.response.UserMembershipResponse;
import com.firstclub.membership.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Subscribe, upgrade, downgrade, or cancel membership")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    @Operation(summary = "Subscribe to a membership plan and tier")
    public ResponseEntity<ApiResponse<UserMembershipResponse>> subscribe(@Valid @RequestBody SubscribeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subscription created successfully", subscriptionService.subscribe(request)));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get active membership for a user")
    public ResponseEntity<ApiResponse<UserMembershipResponse>> getMembership(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(subscriptionService.getMembership(userId)));
    }

    @PutMapping("/{userId}/upgrade-tier")
    @Operation(summary = "Upgrade membership tier (e.g., Silver → Gold)")
    public ResponseEntity<ApiResponse<UserMembershipResponse>> upgradeTier(
            @PathVariable Long userId,
            @Valid @RequestBody ChangeTierRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Tier upgraded successfully", subscriptionService.upgradeTier(userId, request)));
    }

    @PutMapping("/{userId}/downgrade-tier")
    @Operation(summary = "Downgrade membership tier (e.g., Platinum → Gold)")
    public ResponseEntity<ApiResponse<UserMembershipResponse>> downgradeTier(
            @PathVariable Long userId,
            @Valid @RequestBody ChangeTierRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Tier downgraded successfully", subscriptionService.downgradeTier(userId, request)));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Cancel active membership")
    public ResponseEntity<ApiResponse<UserMembershipResponse>> cancelMembership(@PathVariable Long userId) {
        return ResponseEntity.ok(
                ApiResponse.success("Membership cancelled successfully", subscriptionService.cancelMembership(userId)));
    }
}
