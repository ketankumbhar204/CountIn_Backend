package com.countin.countin_backend.meal.application.service;

import com.countin.countin_backend.common.exception.ResourceNotFoundException;
import com.countin.countin_backend.meal.api.dto.request.CreateMealComboRequest;
import com.countin.countin_backend.meal.api.dto.request.UpdateMealComboRequest;
import com.countin.countin_backend.meal.api.dto.response.MealComboResponse;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealComboEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealComboItemEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.MealComboItemRepository;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.MealComboRepository;
import com.countin.countin_backend.space.infrastructure.persistence.entity.SpaceEntity;
import com.countin.countin_backend.space.infrastructure.persistence.repository.SpaceRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MealComboService {

    private final MealComboRepository mealComboRepository;
    private final MealComboItemRepository mealComboItemRepository;
    private final FoodCatalogService foodCatalogService;
    private final MealSpaceSetupService mealSpaceSetupService;
    private final SpaceRepository spaceRepository;
    private final MealAccessService mealAccessService;

    @Transactional
    public List<MealComboResponse> listCombos(UUID spaceId, UUID callerId) {
        mealAccessService.requireViewMeals(spaceId, callerId);
        SpaceEntity space = loadSpace(spaceId);
        mealSpaceSetupService.ensureMessSampleCombos(space);
        return mealComboRepository.findBySpaceIdAndIsActiveTrueOrderByNameAsc(spaceId).stream()
                .map(combo -> MealComboResponse.from(combo, mealComboItemRepository.findByComboIdWithItems(combo.getId())))
                .toList();
    }

    @Transactional
    public MealComboResponse createCombo(UUID spaceId, UUID callerId, CreateMealComboRequest request) {
        mealAccessService.requireManageMeals(spaceId, callerId);
        SpaceEntity space = loadSpace(spaceId);
        MealComboEntity combo = mealComboRepository.save(MealComboEntity.builder()
                .space(space)
                .name(request.getName().trim())
                .description(request.getDescription())
                .isActive(true)
                .build());
        saveComboItems(spaceId, combo, request.getItemIds());
        return MealComboResponse.from(combo, mealComboItemRepository.findByComboIdWithItems(combo.getId()));
    }

    @Transactional
    public MealComboResponse updateCombo(UUID spaceId, UUID comboId, UUID callerId, UpdateMealComboRequest request) {
        mealAccessService.requireManageMeals(spaceId, callerId);
        MealComboEntity combo = loadCombo(spaceId, comboId);
        combo.setName(request.getName().trim());
        combo.setDescription(request.getDescription());
        if (request.getActive() != null) {
            combo.setActive(request.getActive());
        }
        mealComboItemRepository.deleteByComboId(comboId);
        saveComboItems(spaceId, combo, request.getItemIds());
        return MealComboResponse.from(combo, mealComboItemRepository.findByComboIdWithItems(combo.getId()));
    }

    @Transactional
    public void deactivateCombo(UUID spaceId, UUID comboId, UUID callerId) {
        mealAccessService.requireManageMeals(spaceId, callerId);
        MealComboEntity combo = loadCombo(spaceId, comboId);
        combo.setActive(false);
        mealComboRepository.save(combo);
    }

    public MealComboEntity loadCombo(UUID spaceId, UUID comboId) {
        return mealComboRepository
                .findByIdAndSpaceId(comboId, spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("MealCombo", "id", comboId));
    }

    private void saveComboItems(UUID spaceId, MealComboEntity combo, List<UUID> itemIds) {
        int sortOrder = 0;
        for (UUID itemId : itemIds) {
            mealComboItemRepository.save(MealComboItemEntity.builder()
                    .combo(combo)
                    .item(foodCatalogService.loadEnabledItemForSpace(spaceId, itemId))
                    .sortOrder(sortOrder++)
                    .build());
        }
    }

    private SpaceEntity loadSpace(UUID spaceId) {
        return spaceRepository
                .findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space", "id", spaceId));
    }
}
