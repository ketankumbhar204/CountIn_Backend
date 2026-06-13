package com.countin.countin_backend.accommodation.infrastructure.persistence.repository;

import com.countin.countin_backend.accommodation.infrastructure.persistence.entity.AccommodationSetupIdempotencyEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccommodationSetupIdempotencyRepository
        extends JpaRepository<AccommodationSetupIdempotencyEntity, UUID> {

    Optional<AccommodationSetupIdempotencyEntity> findBySpaceIdAndIdempotencyKey(
            UUID spaceId, String idempotencyKey);
}
