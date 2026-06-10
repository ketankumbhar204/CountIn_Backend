package com.countin.countin_backend.user.api.dto.response;

import com.countin.countin_backend.user.infrastructure.persistence.entity.UserEntity;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private UUID id;
    private String mobileNumber;
    private String fullName;
    private String profilePhotoUrl;
    private boolean active;
    private LocalDateTime createdAt;

    public static UserResponse from(UserEntity user) {
        return UserResponse.builder()
                .id(user.getId())
                .mobileNumber(user.getMobileNumber())
                .fullName(user.getFullName())
                .profilePhotoUrl(user.getProfilePhotoUrl())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
