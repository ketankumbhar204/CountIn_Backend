package com.countin.countin_backend.meal.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.countin.countin_backend.common.exception.BusinessException;
import com.countin.countin_backend.meal.domain.model.DailyMenuEntryType;
import com.countin.countin_backend.meal.domain.model.DailyMenuStatus;
import com.countin.countin_backend.meal.domain.model.MealPlanCode;
import com.countin.countin_backend.meal.domain.model.MealParticipationStatus;
import com.countin.countin_backend.meal.domain.model.MealType;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.DailyMenuEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.DailyMenuEntryEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.FoodItemEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealComboEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealComboItemEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealParticipationEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealPlanEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.DailyMenuEntryRepository;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.DailyMenuRepository;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.MealComboItemRepository;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.MealParticipationRepository;
import com.countin.countin_backend.member.application.service.SpaceMembershipResolver;
import com.countin.countin_backend.member.domain.model.MemberStatus;
import com.countin.countin_backend.member.domain.model.MembershipRole;
import com.countin.countin_backend.member.domain.model.MembershipStatus;
import com.countin.countin_backend.member.infrastructure.persistence.entity.MemberEntity;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MealSharePreviewServiceTest {

    @Mock
    private DailyMenuRepository dailyMenuRepository;

    @Mock
    private DailyMenuEntryRepository dailyMenuEntryRepository;

    @Mock
    private MealComboItemRepository mealComboItemRepository;

    @Mock
    private MealParticipationRepository participationRepository;

    @Mock
    private SpaceRepository spaceRepository;

    @Mock
    private SpaceMembershipRepository spaceMembershipRepository;

    @Mock
    private MemberRepository memberRepository;

    private MealSharePreviewService mealSharePreviewService;

    private UUID spaceId;
    private UUID callerId;
    private LocalDate menuDate;

    @BeforeEach
    void setUp() {
        mealSharePreviewService = new MealSharePreviewService(
                dailyMenuRepository,
                dailyMenuEntryRepository,
                mealComboItemRepository,
                participationRepository,
                spaceRepository,
                new MealAccessService(new SpaceMembershipResolver(spaceMembershipRepository), memberRepository));
        spaceId = UUID.randomUUID();
        callerId = UUID.randomUUID();
        menuDate = LocalDate.of(2026, 6, 18);
    }

    @Test
    void getSharePreview_includesComboDetailAndEligibleCount() {
        stubOwnerMembership();
        SpaceEntity space = space();
        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));

        MealComboEntity combo = MealComboEntity.builder().name("Standard Lunch Thali").isActive(true).build();
        combo.setId(UUID.randomUUID());

        DailyMenuEntity published = DailyMenuEntity.builder()
                .space(space)
                .menuDate(menuDate)
                .mealType(MealType.LUNCH)
                .status(DailyMenuStatus.PUBLISHED)
                .notes("Extra salad")
                .isDeleted(false)
                .build();
        published.setId(UUID.randomUUID());

        when(dailyMenuRepository.findBySpaceAndDate(spaceId, menuDate, true, DailyMenuStatus.PUBLISHED))
                .thenReturn(List.of(published));

        DailyMenuEntryEntity comboEntry = DailyMenuEntryEntity.builder()
                .dailyMenu(published)
                .entryType(DailyMenuEntryType.COMBO)
                .combo(combo)
                .label("Standard Lunch Thali")
                .sortOrder(1)
                .isAvailable(true)
                .build();

        when(dailyMenuEntryRepository.findByDailyMenuId(published.getId())).thenReturn(List.of(comboEntry));

        FoodItemEntity chapati = FoodItemEntity.builder().name("Chapati").isActive(true).build();
        FoodItemEntity dal = FoodItemEntity.builder().name("Dal Fry").isActive(true).build();
        when(mealComboItemRepository.findByComboIdWithItems(combo.getId()))
                .thenReturn(List.of(
                        MealComboItemEntity.builder().item(chapati).sortOrder(0).build(),
                        MealComboItemEntity.builder().item(dal).sortOrder(1).build()));

        MealParticipationEntity participation = participation(menuDate);
        when(participationRepository.findAllNonStoppedBySpaceId(spaceId)).thenReturn(List.of(participation));

        var preview = mealSharePreviewService.getSharePreview(spaceId, callerId, menuDate, MealType.LUNCH);

        assertThat(preview.getSpaceName()).isEqualTo("Sunrise Mess");
        assertThat(preview.getStatus()).isEqualTo(DailyMenuStatus.PUBLISHED);
        assertThat(preview.getEligibleCount()).isEqualTo(1);
        assertThat(preview.getSlots()).hasSize(1);
        assertThat(preview.getSlots().get(0).getLines().get(0).getDetail()).isEqualTo("Chapati · Dal Fry");
        assertThat(preview.getMessageText()).contains("Standard Lunch Thali");
        assertThat(preview.getMessageText()).contains("Chapati · Dal Fry");
        assertThat(preview.getMessageText()).contains("Eligible participants: 1");
    }

    @Test
    void getSharePreview_rejectsWhenNoPublishedMenus() {
        stubOwnerMembership();
        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space()));
        when(dailyMenuRepository.findBySpaceAndDate(spaceId, menuDate, true, DailyMenuStatus.PUBLISHED))
                .thenReturn(List.of());

        assertThatThrownBy(() ->
                        mealSharePreviewService.getSharePreview(spaceId, callerId, menuDate, MealType.LUNCH))
                .isInstanceOf(BusinessException.class)
                .hasMessage("No published menus found for the selected date");
    }

    private MealParticipationEntity participation(LocalDate date) {
        MemberEntity member = MemberEntity.builder()
                .fullName("Ravi")
                .mobileNumber("9876543210")
                .role(MembershipRole.TENANT)
                .status(MemberStatus.ACTIVE)
                .isActive(true)
                .build();
        member.setId(UUID.randomUUID());

        MealPlanEntity plan = MealPlanEntity.builder()
                .code(MealPlanCode.FULL)
                .name("Full Meals")
                .breakfastIncluded(true)
                .lunchIncluded(true)
                .dinnerIncluded(true)
                .build();

        return MealParticipationEntity.builder()
                .member(member)
                .mealPlan(plan)
                .status(MealParticipationStatus.ACTIVE)
                .effectiveFrom(date.minusDays(1))
                .build();
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
                .name("Sunrise Mess")
                .type(SpaceType.MESS)
                .isActive(true)
                .build();
        space.setId(spaceId);
        return space;
    }
}
