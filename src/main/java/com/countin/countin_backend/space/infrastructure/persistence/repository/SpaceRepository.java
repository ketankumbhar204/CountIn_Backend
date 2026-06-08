package com.countin.countin_backend.space.infrastructure.persistence.repository;

import com.countin.countin_backend.space.domain.model.SpaceType;
import com.countin.countin_backend.space.infrastructure.persistence.entity.SpaceEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpaceRepository extends JpaRepository<SpaceEntity, UUID> {

    List<SpaceEntity> findByOwnerIdAndIsActiveTrue(UUID ownerId);

    List<SpaceEntity> findByTypeAndIsActiveTrue(SpaceType type);

    Optional<SpaceEntity> findByIdAndIsActiveTrue(UUID id);

    boolean existsByIdAndOwnerIdAndIsActiveTrue(UUID id, UUID ownerId);
}
