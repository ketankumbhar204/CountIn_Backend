package com.countin.countin_backend.member.api.controller;

import com.countin.countin_backend.common.web.ApiResponse;
import com.countin.countin_backend.member.api.dto.request.AcceptInvitationRequest;
import com.countin.countin_backend.member.api.dto.request.CreateInvitationRequest;
import com.countin.countin_backend.member.api.dto.response.InvitationResponse;
import com.countin.countin_backend.member.api.dto.response.SpaceMembershipResponse;
import com.countin.countin_backend.member.application.service.InvitationService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping
    public ResponseEntity<ApiResponse<InvitationResponse>> createInvitation(
            @RequestBody @Valid CreateInvitationRequest request) {
        InvitationResponse response = invitationService.createInvitation(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Invitation sent successfully", response));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<SpaceMembershipResponse>> acceptInvitation(
            @PathVariable UUID id,
            @RequestBody @Valid AcceptInvitationRequest request) {
        SpaceMembershipResponse response = invitationService.acceptInvitation(id, request.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Invitation accepted successfully", response));
    }
}
