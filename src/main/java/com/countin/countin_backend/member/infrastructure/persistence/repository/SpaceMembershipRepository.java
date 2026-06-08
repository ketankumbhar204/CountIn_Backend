package com.countin.countin_backend.member.infrastructure.persistence.repository;

import com.countin.countin_backend.member.domain.model.MembershipRole;
import com.countin.countin_backend.member.domain.model.MembershipStatus;
import com.countin.countin_backend.member.infrastructure.persistence.entity.SpaceMembershipEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpaceMembershipRepository extends JpaRepository<SpaceMembershipEntity, UUID> {

    @Query("SELECT sm FROM SpaceMembershipEntity sm JOIN FETCH sm.space WHERE sm.user.id = :userId")
    List<SpaceMembershipEntity> findByUserIdWithSpace(@Param("userId") UUID userId);

    List<SpaceMembershipEntity> findByUserId(UUID userId);

    List<SpaceMembershipEntity> findBySpaceId(UUID spaceId);

    Optional<SpaceMembershipEntity> findByUserIdAndSpaceId(UUID userId, UUID spaceId);

    List<SpaceMembershipEntity> findBySpaceIdAndStatus(UUID spaceId, MembershipStatus status);

    List<SpaceMembershipEntity> findBySpaceIdAndRole(UUID spaceId, MembershipRole role);

    boolean existsByUserIdAndSpaceIdAndStatus(UUID userId, UUID spaceId, MembershipStatus status);

    boolean existsByUserIdAndSpaceIdAndRoleIn(UUID userId, UUID spaceId, List<MembershipRole> roles);
}
