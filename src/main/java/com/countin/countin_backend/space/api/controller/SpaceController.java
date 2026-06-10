package com.countin.countin_backend.space.api.controller;

import com.countin.countin_backend.common.web.ApiResponse;
import com.countin.countin_backend.space.api.dto.request.CreateSpaceRequest;
import com.countin.countin_backend.space.api.dto.response.SpaceResponse;
import com.countin.countin_backend.space.api.dto.response.UserSpaceResponse;
import com.countin.countin_backend.space.application.service.SpaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/spaces")
@RequiredArgsConstructor
@Tag(name = "Spaces", description = "Space management APIs")
@SecurityRequirement(name = "bearerAuth")
public class SpaceController {

    private final SpaceService spaceService;

    @PostMapping
    @Operation(
            summary = "Create a space",
            description = "Creates a new space and an OWNER membership for the owner. "
                    + "Supported types: PG, MESS, HOSTEL, CO_LIVING, RENTAL.")
    public ResponseEntity<ApiResponse<SpaceResponse>> createSpace(
            @RequestBody @Valid CreateSpaceRequest request) {
        SpaceResponse response = spaceService.createSpace(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Space created successfully", response));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get spaces for a user", description = "Returns all spaces linked to the user via memberships.")
    public ResponseEntity<ApiResponse<List<UserSpaceResponse>>> getUserSpaces(
            @PathVariable UUID userId) {
        List<UserSpaceResponse> spaces = spaceService.getUserSpaces(userId);
        return ResponseEntity.ok(ApiResponse.success(spaces));
    }
}
