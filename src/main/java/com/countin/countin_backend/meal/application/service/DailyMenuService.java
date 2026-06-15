package com.countin.countin_backend.meal.application.service;

import com.countin.countin_backend.common.exception.BusinessException;
import com.countin.countin_backend.common.exception.ResourceNotFoundException;
import com.countin.countin_backend.meal.api.dto.request.CopyDailyMenuRequest;
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
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DailyMenuService {

    private static final int MAX_DATE_RANGE_DAYS = 31;

    private final DailyMenuRepository dailyMenuRepository;
    private final DailyMenuEntryRepository dailyMenuEntryRepository;
    private final MealComboService mealComboService;
    private final FoodCatalogService foodCatalogService;
    private final SpaceRepository spaceRepository;
    private final MealAccessService mealAccessService;

    @Transactional(readOnly = true)
    public List<DailyMenuResponse> listMenus(UUID spaceId, UUID callerId, LocalDate from, LocalDate to) {
        SpaceMembershipEntity membership = mealAccessService.requireViewMeals(spaceId, callerId);
        validateDateRange(from, to);
        boolean publishedOnly = !mealAccessService.canManageMeals(membership);
        return dailyMenuRepository.findBySpaceAndDateRange(spaceId, from, to, publishedOnly).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DailyMenuResponse> getTodayMenus(UUID spaceId, UUID callerId) {
        mealAccessService.requireViewMeals(spaceId, callerId);
        return dailyMenuRepository
                .findBySpaceAndDate(spaceId, LocalDate.now(), true, DailyMenuStatus.PUBLISHED)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DailyMenuResponse> getMenusByDate(UUID spaceId, UUID callerId, LocalDate date) {
        SpaceMembershipEntity membership = mealAccessService.requireViewMeals(spaceId, callerId);
        boolean publishedOnly = !mealAccessService.canManageMeals(membership);
        return dailyMenuRepository
                .findBySpaceAndDate(spaceId, date, publishedOnly, DailyMenuStatus.PUBLISHED)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DailyMenuResponse getMenu(UUID spaceId, UUID callerId, LocalDate date, MealType mealType) {
        SpaceMembershipEntity membership = mealAccessService.requireViewMeals(spaceId, callerId);
        DailyMenuEntity menu = loadMenu(spaceId, date, mealType);
        if (!mealAccessService.canManageMeals(membership)
                && menu.getStatus() != DailyMenuStatus.PUBLISHED) {
            throw new BusinessException("Daily menu is not published yet", HttpStatus.NOT_FOUND);
        }
        return toResponse(menu);
    }

    /**
     * Upserts a daily menu. New menus start as DRAFT. Published menus can be edited in-place without
     * reverting to draft (MVP published-edit policy).
     */
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

        menu.setNotes(request.getNotes());
        menu = dailyMenuRepository.save(menu);
        dailyMenuEntryRepository.deleteByDailyMenuId(menu.getId());
        List<DailyMenuOptionRequest> options =
                request.getOptions() != null ? request.getOptions() : Collections.emptyList();
        saveEntries(spaceId, menu, options);
        return toResponse(menu);
    }

    @Transactional
    public DailyMenuResponse publishMenu(UUID spaceId, UUID callerId, LocalDate date, MealType mealType) {
        mealAccessService.requireManageMeals(spaceId, callerId);
        DailyMenuEntity menu = loadMenu(spaceId, date, mealType);
        if (menu.getStatus() == DailyMenuStatus.PUBLISHED) {
            return toResponse(menu);
        }
        List<DailyMenuEntryEntity> entries = dailyMenuEntryRepository.findByDailyMenuId(menu.getId());
        if (entries.stream().noneMatch(DailyMenuEntryEntity::isAvailable)) {
            throw new BusinessException(
                    "At least one available option is required to publish", HttpStatus.BAD_REQUEST);
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
            throw new BusinessException("Published menus cannot be deleted", HttpStatus.CONFLICT);
        }
        menu.setDeleted(true);
        dailyMenuRepository.save(menu);
    }

    @Transactional
    public DailyMenuResponse copyMenu(
            UUID spaceId,
            UUID callerId,
            LocalDate targetDate,
            MealType mealType,
            LocalDate sourceDate,
            CopyDailyMenuRequest request) {
        mealAccessService.requireManageMeals(spaceId, callerId);
        DailyMenuEntity source = dailyMenuRepository
                .findBySpaceDateAndType(spaceId, sourceDate, mealType)
                .filter(menu -> !menu.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "DailyMenu", "date/mealType", sourceDate + "/" + mealType));

        boolean force = request != null && request.isForce();
        DailyMenuEntity target = dailyMenuRepository
                .findBySpaceDateAndType(spaceId, targetDate, mealType)
                .filter(menu -> !menu.isDeleted())
                .orElse(null);

        if (target != null && target.getStatus() == DailyMenuStatus.PUBLISHED && !force) {
            throw new BusinessException(
                    "Target menu is published; set force=true to overwrite", HttpStatus.CONFLICT);
        }

        SpaceEntity space = loadSpace(spaceId);
        if (target == null) {
            target = DailyMenuEntity.builder()
                    .space(space)
                    .menuDate(targetDate)
                    .mealType(mealType)
                    .isDeleted(false)
                    .build();
        }

        target.setStatus(DailyMenuStatus.DRAFT);
        target.setPublishedAt(null);
        target.setNotes(source.getNotes());
        target = dailyMenuRepository.save(target);

        dailyMenuEntryRepository.deleteByDailyMenuId(target.getId());
        for (DailyMenuEntryEntity sourceEntry :
                dailyMenuEntryRepository.findByDailyMenuId(source.getId())) {
            dailyMenuEntryRepository.save(DailyMenuEntryEntity.builder()
                    .dailyMenu(target)
                    .entryType(sourceEntry.getEntryType())
                    .combo(sourceEntry.getCombo())
                    .item(sourceEntry.getItem())
                    .label(sourceEntry.getLabel())
                    .sortOrder(sourceEntry.getSortOrder())
                    .isAvailable(sourceEntry.isAvailable())
                    .build());
        }

        return toResponse(target);
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
            DailyMenuEntryType entryType = resolveEntryType(option);

            if (entryType == DailyMenuEntryType.COMBO) {
                combo = mealComboService.loadCombo(spaceId, option.getComboId());
                if (!combo.isActive()) {
                    throw new BusinessException("Combo is not active", HttpStatus.BAD_REQUEST);
                }
            } else {
                item = foodCatalogService.loadEnabledItemForSpace(spaceId, option.getItemId());
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

    private DailyMenuEntryType resolveEntryType(DailyMenuOptionRequest option) {
        if (option.getEntryType() != null) {
            if (option.getEntryType() == DailyMenuEntryType.COMBO) {
                if (option.getComboId() == null) {
                    throw new BusinessException("comboId is required for COMBO entries", HttpStatus.BAD_REQUEST);
                }
                if (option.getItemId() != null) {
                    throw new BusinessException("itemId must be null for COMBO entries", HttpStatus.BAD_REQUEST);
                }
                return DailyMenuEntryType.COMBO;
            }
            if (option.getItemId() == null) {
                throw new BusinessException("itemId is required for ITEM entries", HttpStatus.BAD_REQUEST);
            }
            if (option.getComboId() != null) {
                throw new BusinessException("comboId must be null for ITEM entries", HttpStatus.BAD_REQUEST);
            }
            return DailyMenuEntryType.ITEM;
        }
        if (option.getComboId() != null) {
            if (option.getItemId() != null) {
                throw new BusinessException("Provide either comboId or itemId, not both", HttpStatus.BAD_REQUEST);
            }
            return DailyMenuEntryType.COMBO;
        }
        if (option.getItemId() != null) {
            return DailyMenuEntryType.ITEM;
        }
        throw new BusinessException("entryType or comboId/itemId is required", HttpStatus.BAD_REQUEST);
    }

    private void validateDateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new BusinessException("'from' must be on or before 'to'", HttpStatus.BAD_REQUEST);
        }
        long days = ChronoUnit.DAYS.between(from, to) + 1;
        if (days > MAX_DATE_RANGE_DAYS) {
            throw new BusinessException("Date range cannot exceed 31 days", HttpStatus.BAD_REQUEST);
        }
    }

    private SpaceEntity loadSpace(UUID spaceId) {
        return spaceRepository
                .findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space", "id", spaceId));
    }
}
