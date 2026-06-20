package com.countin.countin_backend.dashboard.api.controller;

import com.countin.countin_backend.common.security.SecurityUtils;
import com.countin.countin_backend.common.web.ApiResponse;
import com.countin.countin_backend.dashboard.api.dto.response.MemberPaymentLedgerResponse;
import com.countin.countin_backend.dashboard.application.service.DashboardAccessService;
import com.countin.countin_backend.dashboard.application.service.SpaceBillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/spaces/{spaceId}/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Space payment ledger APIs")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final DashboardAccessService dashboardAccessService;
    private final SpaceBillingService spaceBillingService;

    @GetMapping("/ledger")
    @Operation(
            summary = "Get member payment ledger",
            description = "Returns per-member expected charges, collected amounts, and pending balances "
                    + "for the selected month.")
    public ResponseEntity<ApiResponse<MemberPaymentLedgerResponse>> getLedger(
            @PathVariable UUID spaceId,
            @RequestParam(required = false) String month) {
        UUID callerId = SecurityUtils.getCurrentUserId();
        dashboardAccessService.requireManagePayments(spaceId, callerId);
        MemberPaymentLedgerResponse response = spaceBillingService.buildLedger(spaceId, callerId, month);
        return ResponseEntity.ok(ApiResponse.success("Payment ledger fetched successfully", response));
    }
}
