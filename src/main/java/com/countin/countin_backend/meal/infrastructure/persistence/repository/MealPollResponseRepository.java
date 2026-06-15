package com.countin.countin_backend.meal.infrastructure.persistence.repository;

import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealPollResponseEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealPollResponseRepository extends JpaRepository<MealPollResponseEntity, UUID> {

    Optional<MealPollResponseEntity> findByPollIdAndMemberId(UUID pollId, UUID memberId);

    List<MealPollResponseEntity> findByPollId(UUID pollId);
}
