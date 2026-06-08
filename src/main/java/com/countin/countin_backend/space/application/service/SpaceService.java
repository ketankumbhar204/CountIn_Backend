package com.countin.countin_backend.space.application.service;

import com.countin.countin_backend.common.exception.ResourceNotFoundException;
import com.countin.countin_backend.member.domain.model.MembershipRole;
import com.countin.countin_backend.member.domain.model.MembershipStatus;
import com.countin.countin_backend.member.infrastructure.persistence.entity.SpaceMembershipEntity;
import com.countin.countin_backend.member.infrastructure.persistence.repository.SpaceMembershipRepository;
import com.countin.countin_backend.space.api.dto.request.CreateSpaceRequest;
import com.countin.countin_backend.space.api.dto.response.SpaceResponse;
import com.countin.countin_backend.space.api.dto.response.UserSpaceResponse;
import com.countin.countin_backend.space.infrastructure.persistence.entity.SpaceEntity;
import com.countin.countin_backend.space.infrastructure.persistence.repository.SpaceRepository;
import com.countin.countin_backend.user.infrastructure.persistence.entity.UserEntity;
import com.countin.countin_backend.user.infrastructure.persistence.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;
    private final SpaceMembershipRepository spaceMembershipRepository;

    @Transactional
    public SpaceResponse createSpace(CreateSpaceRequest request) {
        UserEntity owner = userRepository.findByIdAndIsActiveTrue(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getOwnerId()));

        SpaceEntity space = SpaceEntity.builder()
                .owner(owner)
                .name(request.getName())
                .type(request.getType())
                .address(request.getAddress())
                .contactNumber(request.getContactNumber())
                .build();

        space = spaceRepository.save(space);

        SpaceMembershipEntity ownerMembership = SpaceMembershipEntity.builder()
                .user(owner)
                .space(space)
                .role(MembershipRole.OWNER)
                .status(MembershipStatus.ACTIVE)
                .joinedAt(LocalDateTime.now())
                .build();

        spaceMembershipRepository.save(ownerMembership);

        return SpaceResponse.from(space);
    }

    @Transactional(readOnly = true)
    public List<UserSpaceResponse> getUserSpaces(UUID userId) {
        return spaceMembershipRepository.findByUserIdWithSpace(userId)
                .stream()
                .map(UserSpaceResponse::from)
                .toList();
    }
}
