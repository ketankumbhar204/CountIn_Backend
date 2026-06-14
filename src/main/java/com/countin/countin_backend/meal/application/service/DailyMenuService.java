package com.countin.countin_backend.meal.application.service;

import com.countin.countin_backend.common.exception.BusinessException;
import com.countin.countin_backend.common.exception.ResourceNotFoundException;
import com.countin.countin_backend.meal.api.dto.request.DailyMenuOptionRequest;
import com.countin.countin_backend.meal.api.dto.request.UpsertDailyMenuRequest;
import com.countin.countin_backend.meal.api.dto.response.DailyMenuResponse;
import com.countin.countin_backend.meal.domain.model.DailyMenuEntryType;
import com.countin.countin_backend.meal.domain.model.DailyMenuStatus;
import com.countin.countin_backend.meal.domain.model.MealType;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.DailyMenuEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.DailyMenuEntryEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.FoodItemEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealComboEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.DailyMenuEntryRepository;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.DailyMenuRepository;
import com.countin.countin_backend.member.infrastructure.persistence.entity.SpaceMembershipEntity;
import com.countin.countin_backend.space.infrastructure.persistence.entity.SpaceEntity;
import com.countin.countin_backend.space.infrastructure.persistence.repository.SpaceRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DailyMenuService {

    private final DailyMenuRepository dailyMenuRepository;
    private final DailyMenuEntryRepository dailyMenuEntryRepository;
    private final MealComboService mealComboService;
    private final FoodCatalogService foodCatalogService;
    private final SpaceRepository spaceRepository;
    private final MealAccessService mealAccessService;

    @Transactional(readOnly = true)
    public List<DailyMenuResponse> listMenus(UUID spaceId, UUID callerId, LocalDate from, LocalDate to) {
        SpaceMembershipEntity membership = mealAccessService.requireViewMeals(spaceId, callerId);
        boolean publishedOnly = mealAccessService.isParticipantScopeOnly(membership);
        return dailyMenuRepository.findBySpaceAndDateRange(spaceId, from, to, publishedOnly).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DailyMenuResponse> getTodayMenus(UUID spaceId, UUID callerId) {
        return listMenus(spaceId, callerId, LocalDate.now(), LocalDate.now());
    }

    @Transactional(readOnly = true)
    public DailyMenuResponse getMenu(UUID spaceId, UUID callerId, LocalDate date, MealType mealType) {
        SpaceMembershipEntity membership = mealAccessService.requireViewMeals(spaceId, callerId);
        DailyMenuEntity menu = loadMenu(spaceId, date, mealType);
        if (mealAccessService.isParticipantScopeOnly(membership)
                && menu.getStatus() != DailyMenuStatus.PUBLISHED) {
            throw new BusinessException("Daily menu is not published yet", HttpStatus.NOT_FOUND);
        }
        return toResponse(menu);
    }

    @Transactional
    public DailyMenuResponse upsertMenu(
            UUID spaceId, UUID callerId, LocalDate date, MealType mealType, UpsertDailyMenuRequest request) {
        mealAccessService.requireManageMeals(spaceId, callerId);
        SpaceEntity space = loadSpace(spaceId);
        DailyMenuEntity menu = dailyMenuRepository
                .findBySpaceDateAndType(spaceId, date, mealType)
                .orElseGet(() -> DailyMenuEntity.builder()
                        .space(space)
                        .menuDate(date)
                        .mealType(mealType)
                        .status(DailyMenuStatus.DRAFT)
                        .isDeleted(false)
                        .build());

        if (menu.isDeleted()) {
            menu.setDeleted(false);
            menu.setStatus(DailyMenuStatus.DRAFT);
            menu.setPublishedAt(null);
        }
        if (menu.getStatus() == DailyMenuStatus.PUBLISHED) {
            throw new BusinessException("Published menus cannot be edited; delete draft first");
        }

        menu.setNotes(request.getNotes());
        menu = dailyMenuRepository.save(menu);
        dailyMenuEntryRepository.deleteByDailyMenuId(menu.getId());
        saveEntries(spaceId, menu, request.getOptions());
        return toResponse(menu);
    }

    @Transactional
    public DailyMenuResponse publishMenu(UUID spaceId, UUID callerId, LocalDate date, MealType mealType) {
        mealAccessService.requireManageMeals(spaceId, callerId);
        DailyMenuEntity menu = loadMenu(spaceId, date, mealType);
        if (menu.getStatus() == DailyMenuStatus.PUBLISHED) {
            return toResponse(menu);
        }
        menu.setStatus(DailyMenuStatus.PUBLISHED);
        menu.setPublishedAt(LocalDateTime.now());
        return toResponse(dailyMenuRepository.save(menu));
    }

    @Transactional
    public void deleteDraftMenu(UUID spaceId, UUID callerId, LocalDate date, MealType mealType) {
        mealAccessService.requireManageMeals(spaceId, callerId);
        DailyMenuEntity menu = loadMenu(spaceId, date, mealType);
        if (menu.getStatus() == DailyMenuStatus.PUBLISHED) {
            throw new BusinessException("Published menus cannot be deleted");
        }
        menu.setDeleted(true);
        dailyMenuRepository.save(menu);
    }

    public boolean isPublished(UUID spaceId, LocalDate date, MealType mealType) {
        return dailyMenuRepository
                .findBySpaceDateAndType(spaceId, date, mealType)
                .filter(menu -> !menu.isDeleted() && menu.getStatus() == DailyMenuStatus.PUBLISHED)
                .isPresent();
    }

    private DailyMenuResponse toResponse(DailyMenuEntity menu) {
        return DailyMenuResponse.from(menu, dailyMenuEntryRepository.findByDailyMenuId(menu.getId()));
    }

    private DailyMenuEntity loadMenu(UUID spaceId, LocalDate date, MealType mealType) {
        return dailyMenuRepository
                .findBySpaceDateAndType(spaceId, date, mealType)
                .filter(menu -> !menu.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "DailyMenu", "date/mealType", date + "/" + mealType));
    }

    private void saveEntries(UUID spaceId, DailyMenuEntity menu, List<DailyMenuOptionRequest> options) {
        for (DailyMenuOptionRequest option : options) {
            MealComboEntity combo = null;
            FoodItemEntity item = null;
            DailyMenuEntryType entryType;
            if (option.getComboId() != null) {
                combo = mealComboService.loadCombo(spaceId, option.getComboId());
                entryType = DailyMenuEntryType.COMBO;
            } else if (option.getItemId() != null) {
                item = foodCatalogService.loadEnabledItemForSpace(spaceId, option.getItemId());
                entryType = DailyMenuEntryType.ITEM;
            } else {
                entryType = DailyMenuEntryType.ITEM;
            }
            dailyMenuEntryRepository.save(DailyMenuEntryEntity.builder()
                    .dailyMenu(menu)
                    .entryType(entryType)
                    .combo(combo)
                    .item(item)
                    .label(option.getLabel().trim())
                    .sortOrder(option.getSortOrder())
                    .isAvailable(option.isAvailable())
                    .build());
        }
    }

    private SpaceEntity loadSpace(UUID spaceId) {
        return spaceRepository
                .findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space", "id", spaceId));
    }
}
