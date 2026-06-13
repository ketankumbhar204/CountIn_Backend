package com.countin.countin_backend.occupancy.application.service;

import com.countin.countin_backend.common.exception.BusinessException;
import com.countin.countin_backend.member.domain.model.MembershipRole;
import com.countin.countin_backend.member.domain.model.MembershipStatus;
import com.countin.countin_backend.member.infrastructure.persistence.entity.MemberEntity;
import com.countin.countin_backend.member.infrastructure.persistence.repository.SpaceMembershipRepository;
import com.countin.countin_backend.space.infrastructure.persistence.entity.SpaceEntity;
import com.countin.countin_backend.space.infrastructure.persistence.repository.SpaceRepository;
import com.countin.countin_backend.common.exception.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OccupancyAccessService {

    private final SpaceRepository spaceRepository;
    private final SpaceMembershipRepository spaceMembershipRepository;

    public SpaceEntity loadSpace(UUID spaceId) {
        return spaceRepository.findByIdAndIsActiveTrue(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space", "id", spaceId));
    }

    public void assertCanManageOccupancy(UUID spaceId, UUID callerId) {
        assertRoleIn(spaceId, callerId, List.of(MembershipRole.OWNER, MembershipRole.MANAGER));
    }

    public void assertCanViewSpaceOccupancies(UUID spaceId, UUID callerId) {
        if (isRole(spaceId, callerId, MembershipRole.CUSTOMER)) {
            throw new BusinessException("Customers cannot access occupancy data", HttpStatus.FORBIDDEN);
        }
        if (isRole(spaceId, callerId, MembershipRole.TENANT)) {
            throw new BusinessException("Use member-specific occupancy endpoints", HttpStatus.FORBIDDEN);
        }
        assertActiveMembership(spaceId, callerId);
    }

    public void assertCanViewMemberOccupancy(UUID spaceId, UUID memberId, UUID callerId, MemberEntity member) {
        if (isRole(spaceId, callerId, MembershipRole.CUSTOMER)) {
            throw new BusinessException("Customers cannot access occupancy data", HttpStatus.FORBIDDEN);
        }
        if (isRole(spaceId, callerId, MembershipRole.TENANT)) {
            if (member.getUser() == null || !member.getUser().getId().equals(callerId)) {
                throw new BusinessException("Tenants can only view their own occupancy", HttpStatus.FORBIDDEN);
            }
            return;
        }
        assertActiveMembership(spaceId, callerId);
    }

    private void assertActiveMembership(UUID spaceId, UUID callerId) {
        if (!spaceMembershipRepository.existsByUserIdAndSpaceIdAndStatus(
                callerId, spaceId, MembershipStatus.ACTIVE)) {
            throw new BusinessException("You are not an active member of this space", HttpStatus.FORBIDDEN);
        }
    }

    private void assertRoleIn(UUID spaceId, UUID callerId, List<MembershipRole> roles) {
        if (!spaceMembershipRepository.existsByUserIdAndSpaceIdAndRoleIn(callerId, spaceId, roles)) {
            throw new BusinessException("Only OWNER or MANAGER can perform this action", HttpStatus.FORBIDDEN);
        }
    }

    private boolean isRole(UUID spaceId, UUID callerId, MembershipRole role) {
        return spaceMembershipRepository.existsByUserIdAndSpaceIdAndRoleIn(
                callerId, spaceId, List.of(role));
    }
}
