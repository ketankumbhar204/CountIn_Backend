package com.countin.countin_backend.meal.api.controller;

import com.countin.countin_backend.common.security.SecurityUtils;
import com.countin.countin_backend.common.web.ApiResponse;
import com.countin.countin_backend.meal.api.dto.response.EligibleParticipantResponse;
import com.countin.countin_backend.meal.api.dto.response.MealEligibilitySummaryResponse;
import com.countin.countin_backend.meal.application.service.MealEligibilityService;
import com.countin.countin_backend.meal.domain.model.MealType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/spaces/{spaceId}/meals")
@RequiredArgsConstructor
@Tag(name = "Meal Eligibility", description = "Poll audience eligibility (Phase 5)")
@SecurityRequirement(name = "bearerAuth")
public class MealEligibilityController {

    private final MealEligibilityService mealEligibilityService;

    @GetMapping("/eligibility-summary")
    public ResponseEntity<ApiResponse<MealEligibilitySummaryResponse>> getSummary(
            @org.springframework.web.bind.annotation.PathVariable UUID spaceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        UUID callerId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Meal eligibility summary fetched successfully",
                mealEligibilityService.getSummary(spaceId, callerId, date)));
    }

    @GetMapping("/eligible-participants")
    public ResponseEntity<ApiResponse<List<EligibleParticipantResponse>>> listEligibleParticipants(
            @org.springframework.web.bind.annotation.PathVariable UUID spaceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam MealType mealType) {
        UUID callerId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Eligible participants fetched successfully",
                mealEligibilityService.listEligibleParticipants(spaceId, callerId, date, mealType)));
    }
}
