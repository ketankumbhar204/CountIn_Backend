package com.countin.countin_backend.member.infrastructure.persistence.repository;

import com.countin.countin_backend.member.domain.model.InvitationStatus;
import com.countin.countin_backend.member.infrastructure.persistence.entity.InvitationEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationRepository extends JpaRepository<InvitationEntity, UUID> {

    List<InvitationEntity> findByMobileNumber(String mobileNumber);

    List<InvitationEntity> findByStatus(InvitationStatus status);

    List<InvitationEntity> findBySpaceId(UUID spaceId);

    List<InvitationEntity> findBySpaceIdAndStatus(UUID spaceId, InvitationStatus status);

    Optional<InvitationEntity> findBySpaceIdAndMobileNumberAndStatus(
            UUID spaceId, String mobileNumber, InvitationStatus status);

    List<InvitationEntity> findByStatusAndExpiresAtBefore(InvitationStatus status, Instant now);

    boolean existsBySpaceIdAndMobileNumberAndStatus(
            UUID spaceId, String mobileNumber, InvitationStatus status);
}
