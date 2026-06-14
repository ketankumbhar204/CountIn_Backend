package com.countin.countin_backend.meal.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.countin.countin_backend.meal.api.dto.request.DailyMenuOptionRequest;
import com.countin.countin_backend.meal.api.dto.request.UpsertDailyMenuRequest;
import com.countin.countin_backend.meal.domain.model.DailyMenuEntryType;
import com.countin.countin_backend.meal.domain.model.MealType;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.DailyMenuEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.DailyMenuEntryEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.FoodItemEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.DailyMenuEntryRepository;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.DailyMenuRepository;
import com.countin.countin_backend.member.application.service.SpaceMembershipResolver;
import com.countin.countin_backend.member.domain.model.MembershipRole;
import com.countin.countin_backend.member.domain.model.MembershipStatus;
import com.countin.countin_backend.member.infrastructure.persistence.entity.SpaceMembershipEntity;
import com.countin.countin_backend.member.infrastructure.persistence.repository.MemberRepository;
import com.countin.countin_backend.member.infrastructure.persistence.repository.SpaceMembershipRepository;
import com.countin.countin_backend.space.domain.model.SpaceType;
import com.countin.countin_backend.space.infrastructure.persistence.entity.SpaceEntity;
import com.countin.countin_backend.space.infrastructure.persistence.repository.SpaceRepository;
import com.countin.countin_backend.user.infrastructure.persistence.entity.UserEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DailyMenuServiceTest {

    @Mock
    private DailyMenuRepository dailyMenuRepository;

    @Mock
    private DailyMenuEntryRepository dailyMenuEntryRepository;

    @Mock
    private MealComboService mealComboService;

    @Mock
    private FoodCatalogService foodCatalogService;

    @Mock
    private SpaceRepository spaceRepository;

    @Mock
    private SpaceMembershipRepository spaceMembershipRepository;

    @Mock
    private MemberRepository memberRepository;

    private DailyMenuService dailyMenuService;

    private UUID spaceId;
    private UUID callerId;
    private UUID itemId;

    @BeforeEach
    void setUp() {
        dailyMenuService = new DailyMenuService(
                dailyMenuRepository,
                dailyMenuEntryRepository,
                mealComboService,
                foodCatalogService,
                spaceRepository,
                new MealAccessService(new SpaceMembershipResolver(spaceMembershipRepository), memberRepository));
        spaceId = UUID.randomUUID();
        callerId = UUID.randomUUID();
        itemId = UUID.randomUUID();
    }

    @Test
    void upsertMenu_persistsItemEntryAndRoundTripsOnGet() {
        stubOwnerMembership();
        LocalDate menuDate = LocalDate.of(2026, 7, 15);
        SpaceEntity space = space();
        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));

        DailyMenuEntity savedMenu = DailyMenuEntity.builder()
                .space(space)
                .menuDate(menuDate)
                .mealType(MealType.LUNCH)
                .isDeleted(false)
                .build();
        savedMenu.setId(UUID.randomUUID());
        when(dailyMenuRepository.findBySpaceDateAndType(spaceId, menuDate, MealType.LUNCH))
                .thenReturn(Optional.empty(), Optional.of(savedMenu));
        when(dailyMenuRepository.save(any(DailyMenuEntity.class))).thenReturn(savedMenu);

        FoodItemEntity item = FoodItemEntity.builder().name("Green Salad").isActive(true).build();
        item.setId(itemId);
        when(foodCatalogService.loadEnabledItemForSpace(spaceId, itemId)).thenReturn(item);

        DailyMenuOptionRequest option = new DailyMenuOptionRequest();
        option.setItemId(itemId);
        option.setLabel("Extra Salad");
        option.setSortOrder(2);
        option.setAvailable(true);

        UpsertDailyMenuRequest request = new UpsertDailyMenuRequest();
        request.setOptions(List.of(option));
        request.setNotes("Extra salad today");

        dailyMenuService.upsertMenu(spaceId, callerId, menuDate, MealType.LUNCH, request);

        ArgumentCaptor<DailyMenuEntryEntity> entryCaptor = ArgumentCaptor.forClass(DailyMenuEntryEntity.class);
        verify(dailyMenuEntryRepository).save(entryCaptor.capture());
        DailyMenuEntryEntity savedEntry = entryCaptor.getValue();
        assertThat(savedEntry.getEntryType()).isEqualTo(DailyMenuEntryType.ITEM);
        assertThat(savedEntry.getItem()).isEqualTo(item);
        assertThat(savedEntry.getCombo()).isNull();
        assertThat(savedEntry.getLabel()).isEqualTo("Extra Salad");

        DailyMenuEntryEntity persistedEntry = DailyMenuEntryEntity.builder()
                .dailyMenu(savedMenu)
                .entryType(DailyMenuEntryType.ITEM)
                .item(item)
                .label("Extra Salad")
                .sortOrder(2)
                .isAvailable(true)
                .build();
        persistedEntry.setId(UUID.randomUUID());
        when(dailyMenuEntryRepository.findByDailyMenuId(savedMenu.getId())).thenReturn(List.of(persistedEntry));

        var response = dailyMenuService.getMenu(spaceId, callerId, menuDate, MealType.LUNCH);

        assertThat(response.getOptions()).hasSize(1);
        assertThat(response.getOptions().get(0).getEntryType()).isEqualTo(DailyMenuEntryType.ITEM);
        assertThat(response.getOptions().get(0).getItemId()).isEqualTo(itemId);
        assertThat(response.getOptions().get(0).getComboId()).isNull();
        assertThat(response.getOptions().get(0).getLabel()).isEqualTo("Extra Salad");
    }

    private void stubOwnerMembership() {
        UserEntity user = UserEntity.builder().fullName("Owner").mobileNumber("9000000000").build();
        user.setId(callerId);
        SpaceEntity space = space();
        SpaceMembershipEntity membership = SpaceMembershipEntity.builder()
                .user(user)
                .space(space)
                .role(MembershipRole.OWNER)
                .status(MembershipStatus.ACTIVE)
                .build();
        when(spaceMembershipRepository.findMembershipByUserAndSpace(callerId, spaceId))
                .thenReturn(Optional.of(membership));
    }

    private SpaceEntity space() {
        UserEntity owner = UserEntity.builder().fullName("Owner").mobileNumber("9000000001").build();
        owner.setId(UUID.randomUUID());
        SpaceEntity space = SpaceEntity.builder()
                .owner(owner)
                .name("Mess")
                .type(SpaceType.MESS)
                .isActive(true)
                .build();
        space.setId(spaceId);
        return space;
    }
}
