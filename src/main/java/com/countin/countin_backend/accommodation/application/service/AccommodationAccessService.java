package com.countin.countin_backend.accommodation.application.service;

import com.countin.countin_backend.accommodation.domain.model.AccommodationProfile;
import com.countin.countin_backend.accommodation.domain.policy.AccommodationProfileResolver;
import com.countin.countin_backend.common.exception.BusinessException;
import com.countin.countin_backend.common.exception.ResourceNotFoundException;
import com.countin.countin_backend.member.domain.model.MembershipRole;
import com.countin.countin_backend.member.domain.model.MembershipStatus;
import com.countin.countin_backend.member.infrastructure.persistence.repository.SpaceMembershipRepository;
import com.countin.countin_backend.space.domain.model.SpaceType;
import com.countin.countin_backend.space.infrastructure.persistence.entity.SpaceEntity;
import com.countin.countin_backend.space.infrastructure.persistence.repository.SpaceRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationAccessService {

    private final SpaceRepository spaceRepository;
    private final SpaceMembershipRepository spaceMembershipRepository;
    private final AccommodationProfileResolver profileResolver;

    public SpaceEntity loadAccommodationSpace(UUID spaceId) {
        SpaceEntity space = spaceRepository.findByIdAndIsActiveTrue(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space", "id", spaceId));
        if (!profileResolver.isAccommodationApplicable(space.getType())) {
            throw new BusinessException(
                    "Accommodation is not applicable for Mess spaces", HttpStatus.BAD_REQUEST);
        }
        return space;
    }

    public AccommodationProfile loadProfile(UUID spaceId) {
        SpaceEntity space = loadAccommodationSpace(spaceId);
        return profileResolver.resolve(space.getType());
    }

    public SpaceType loadSpaceType(UUID spaceId) {
        return loadAccommodationSpace(spaceId).getType();
    }

    public void assertCallerBelongsToSpace(UUID spaceId, UUID callerId) {
        loadAccommodationSpace(spaceId);
        boolean belongs = spaceMembershipRepository.existsByUserIdAndSpaceIdAndStatus(
                callerId, spaceId, MembershipStatus.ACTIVE);
        if (!belongs) {
            throw new BusinessException(
                    "You do not have access to this space", HttpStatus.FORBIDDEN);
        }
    }

    public void assertOwnerOrManager(UUID spaceId, UUID callerId) {
        loadAccommodationSpace(spaceId);
        boolean allowed = spaceMembershipRepository.existsByUserIdAndSpaceIdAndRoleIn(
                callerId, spaceId, List.of(MembershipRole.OWNER, MembershipRole.MANAGER));
        if (!allowed) {
            throw new BusinessException(
                    "Only OWNER or MANAGER can perform this action", HttpStatus.FORBIDDEN);
        }
    }

    public void assertCallerIsOwner(UUID spaceId, UUID callerId) {
        loadAccommodationSpace(spaceId);
        boolean isOwner = spaceMembershipRepository.existsByUserIdAndSpaceIdAndRoleIn(
                callerId, spaceId, List.of(MembershipRole.OWNER));
        if (!isOwner) {
            throw new BusinessException(
                    "Only the space owner can perform this action", HttpStatus.FORBIDDEN);
        }
    }
}
