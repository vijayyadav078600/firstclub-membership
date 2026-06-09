package com.firstclub.membership.controller;

import com.firstclub.membership.dto.response.ApiResponse;
import com.firstclub.membership.dto.response.TierEvaluationResponse;
import com.firstclub.membership.enums.TierType;
import com.firstclub.membership.service.TierEvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tier-evaluation")
@RequiredArgsConstructor
@Tag(name = "Tier Evaluation", description = "Evaluate and auto-update user tier based on activity criteria")
public class TierEvaluationController {

    private final TierEvaluationService tierEvaluationService;

    @PostMapping("/{userId}")
    @Operation(summary = "Evaluate and apply the qualifying tier for a user based on configured criteria")
    public ResponseEntity<ApiResponse<TierEvaluationResponse>> evaluateAndUpdate(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(tierEvaluationService.evaluateAndUpdateTier(userId)));
    }

    @GetMapping("/{userId}/preview")
    @Operation(summary = "Preview which tier a user would qualify for (without applying changes)")
    public ResponseEntity<ApiResponse<TierType>> previewQualifyingTier(@PathVariable Long userId) {
        TierType qualifying = tierEvaluationService.evaluateQualifyingTier(userId);
        return ResponseEntity.ok(ApiResponse.success("Qualifying tier for user " + userId, qualifying));
    }
}
