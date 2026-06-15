package com.countin.countin_backend.meal.api.controller;

import com.countin.countin_backend.common.security.SecurityUtils;
import com.countin.countin_backend.common.web.ApiResponse;
import com.countin.countin_backend.meal.api.dto.request.SubmitMealPollResponsesRequest;
import com.countin.countin_backend.meal.api.dto.response.MealPollDayResponse;
import com.countin.countin_backend.meal.api.dto.response.MealPollResponse;
import com.countin.countin_backend.meal.application.service.MealPollService;
import com.countin.countin_backend.meal.domain.model.MealType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/spaces/{spaceId}/meal-polls")
@RequiredArgsConstructor
@Tag(name = "Meal Polls", description = "Menu option polls and member responses (Phase 6)")
@SecurityRequirement(name = "bearerAuth")
public class MealPollController {

    private final MealPollService mealPollService;

    @GetMapping
    public ResponseEntity<ApiResponse<MealPollDayResponse>> listPolls(
            @PathVariable UUID spaceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        UUID callerId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Meal polls fetched successfully", mealPollService.getPollsForDate(spaceId, callerId, date)));
    }

    @GetMapping("/{date}/{mealType}")
    public ResponseEntity<ApiResponse<MealPollResponse>> getPoll(
            @PathVariable UUID spaceId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable MealType mealType) {
        UUID callerId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Meal poll fetched successfully", mealPollService.getPoll(spaceId, callerId, date, mealType)));
    }

    @PostMapping("/{date}/{mealType}/open")
    public ResponseEntity<ApiResponse<MealPollResponse>> openPoll(
            @PathVariable UUID spaceId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable MealType mealType) {
        UUID callerId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Meal poll opened successfully", mealPollService.openPoll(spaceId, callerId, date, mealType)));
    }

    @PostMapping("/{date}/{mealType}/close")
    public ResponseEntity<ApiResponse<MealPollResponse>> closePoll(
            @PathVariable UUID spaceId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable MealType mealType) {
        UUID callerId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Meal poll closed successfully", mealPollService.closePoll(spaceId, callerId, date, mealType)));
    }

    @PostMapping("/{date}/responses")
    public ResponseEntity<ApiResponse<MealPollDayResponse>> submitResponses(
            @PathVariable UUID spaceId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody @Valid SubmitMealPollResponsesRequest request) {
        UUID callerId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                "Meal poll responses saved successfully",
                mealPollService.submitResponses(spaceId, callerId, date, request.getSelections())));
    }
}
