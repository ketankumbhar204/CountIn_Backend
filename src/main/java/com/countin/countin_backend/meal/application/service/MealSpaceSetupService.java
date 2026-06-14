package com.countin.countin_backend.meal.application.service;

import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealComboEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealComboItemEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.FoodItemRepository;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.MealComboItemRepository;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.MealComboRepository;
import com.countin.countin_backend.space.domain.model.SpaceType;
import com.countin.countin_backend.space.infrastructure.persistence.entity.SpaceEntity;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealSpaceSetupService {

    private static final UUID CHAPATI_ID = UUID.fromString("22222222-2222-2222-2222-222222220001");
    private static final UUID DAL_FRY_ID = UUID.fromString("22222222-2222-2222-2222-222222220020");
    private static final UUID PLAIN_RICE_ID = UUID.fromString("22222222-2222-2222-2222-222222220010");
    private static final UUID GREEN_SALAD_ID = UUID.fromString("22222222-2222-2222-2222-222222220100");

    private final MealComboRepository mealComboRepository;
    private final MealComboItemRepository mealComboItemRepository;
    private final FoodItemRepository foodItemRepository;

    @Transactional
    public void ensureMessSampleCombos(SpaceEntity space) {
        if (space.getType() != SpaceType.MESS) {
            return;
        }
        if (!mealComboRepository.findBySpaceIdAndIsActiveTrueOrderByNameAsc(space.getId()).isEmpty()) {
            return;
        }
        if (foodItemRepository.findGlobalByName("Chapati").isEmpty()) {
            log.warn(
                    "Cannot seed MESS sample combos for space {} — global food catalog is missing. "
                            + "Apply Flyway V40 or V43.",
                    space.getId());
            return;
        }
        log.info("Seeding sample meal combos for MESS space {}", space.getId());
        createCombo(
                space,
                "Standard Lunch Thali",
                "Daily lunch combo",
                List.of(CHAPATI_ID, DAL_FRY_ID, PLAIN_RICE_ID, GREEN_SALAD_ID));
        createCombo(space, "Dal Rice Combo", "Simple dal rice", List.of(DAL_FRY_ID, PLAIN_RICE_ID));
    }

    private void createCombo(SpaceEntity space, String name, String description, List<UUID> itemIds) {
        MealComboEntity combo = mealComboRepository.save(MealComboEntity.builder()
                .space(space)
                .name(name)
                .description(description)
                .isActive(true)
                .build());
        for (int index = 0; index < itemIds.size(); index++) {
            UUID itemId = itemIds.get(index);
            int sortOrder = index;
            foodItemRepository.findByIdWithCategory(itemId).ifPresent(item -> mealComboItemRepository.save(
                    MealComboItemEntity.builder()
                            .combo(combo)
                            .item(item)
                            .sortOrder(sortOrder)
                            .build()));
        }
    }
}
