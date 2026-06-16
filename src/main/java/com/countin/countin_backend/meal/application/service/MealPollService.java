package com.countin.countin_backend.meal.application.service;

import com.countin.countin_backend.common.exception.BusinessException;
import com.countin.countin_backend.meal.api.dto.request.SubmitMealPollSelectionRequest;
import com.countin.countin_backend.meal.api.dto.response.MealPollDayResponse;
import com.countin.countin_backend.meal.api.dto.response.MealPollOptionResponse;
import com.countin.countin_backend.meal.api.dto.response.MealPollResponse;
import com.countin.countin_backend.meal.domain.model.DailyMenuEntryType;
import com.countin.countin_backend.meal.domain.model.DailyMenuStatus;
import com.countin.countin_backend.meal.domain.model.MealPollOptionType;
import com.countin.countin_backend.meal.domain.model.MealPollResponseSource;
import com.countin.countin_backend.meal.domain.model.MealPollStatus;
import com.countin.countin_backend.meal.domain.model.MealType;
import com.countin.countin_backend.meal.domain.policy.MealEligibilityEngine;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.DailyMenuEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.DailyMenuEntryEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealComboItemEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealPollEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealPollOptionEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealPollResponseEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.entity.MealParticipationEntity;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.DailyMenuEntryRepository;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.DailyMenuRepository;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.DailyMenuPackageItemRepository;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.MealComboItemRepository;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.MealParticipationRepository;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.MealPollOptionRepository;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.MealPollRepository;
import com.countin.countin_backend.meal.infrastructure.persistence.repository.MealPollResponseRepository;
import com.countin.countin_backend.member.infrastructure.persistence.entity.MemberEntity;
import com.countin.countin_backend.member.infrastructure.persistence.entity.SpaceMembershipEntity;
import com.countin.countin_backend.member.infrastructure.persistence.repository.MemberRepository;
import com.countin.countin_backend.space.infrastructure.persistence.entity.SpaceEntity;
import com.countin.countin_backend.space.infrastructure.persistence.repository.SpaceRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MealPollService {

    private final MealPollRepository pollRepository;
    private final MealPollOptionRepository optionRepository;
    private final MealPollResponseRepository responseRepository;
    private final DailyMenuRepository dailyMenuRepository;
    private final DailyMenuEntryRepository dailyMenuEntryRepository;
    private final MealComboItemRepository mealComboItemRepository;
    private final DailyMenuPackageItemRepository dailyMenuPackageItemRepository;
    private final MealParticipationRepository participationRepository;
    private final SpaceRepository spaceRepository;
    private final MemberRepository memberRepository;
    private final MealAccessService mealAccessService;

    @Transactional(readOnly = true)
    public MealPollDayResponse getPollsForDate(UUID spaceId, UUID callerId, LocalDate date) {
        mealAccessService.requireViewMeals(spaceId, callerId);
        LocalDate pollDate = date != null ? date : LocalDate.now();
        UUID memberId = resolveMemberIdIfParticipant(spaceId, callerId);

        List<MealPollResponse> polls = pollRepository.findBySpaceIdAndPollDateOrderByMealTypeAsc(spaceId, pollDate)
                .stream()
                .map(poll -> toPollResponse(poll, memberId))
                .toList();

        return MealPollDayResponse.builder().pollDate(pollDate).polls(polls).build();
    }

    @Transactional(readOnly = true)
    public MealPollResponse getPoll(UUID spaceId, UUID callerId, LocalDate date, MealType mealType) {
        mealAccessService.requireViewMeals(spaceId, callerId);
        UUID memberId = resolveMemberIdIfParticipant(spaceId, callerId);
        MealPollEntity poll = loadPoll(spaceId, date, mealType);
        return toPollResponse(poll, memberId);
    }

    @Transactional
    public MealPollResponse openPoll(UUID spaceId, UUID callerId, LocalDate date, MealType mealType) {
        mealAccessService.requireManageMeals(spaceId, callerId);
        LocalDate pollDate = date != null ? date : LocalDate.now();

        DailyMenuEntity menu = dailyMenuRepository
                .findBySpaceDateAndType(spaceId, pollDate, mealType)
                .filter(m -> !m.isDeleted() && m.getStatus() == DailyMenuStatus.PUBLISHED)
                .orElseThrow(() -> new BusinessException(
                        "Publish the menu before opening a poll", HttpStatus.BAD_REQUEST));

        List<DailyMenuEntryEntity> entries = dailyMenuEntryRepository.findByDailyMenuId(menu.getId()).stream()
                .filter(DailyMenuEntryEntity::isAvailable)
                .toList();
        if (entries.isEmpty()) {
            throw new BusinessException("Menu has no available options to poll", HttpStatus.BAD_REQUEST);
        }

        Optional<MealPollEntity> existing =
                pollRepository.findBySpaceIdAndPollDateAndMealType(spaceId, pollDate, mealType);
        if (existing.isPresent()) {
            MealPollEntity poll = existing.get();
            if (poll.getStatus() == MealPollStatus.OPEN) {
                return toPollResponse(poll, null);
            }
            throw new BusinessException("Poll already closed for this meal slot", HttpStatus.CONFLICT);
        }

        SpaceEntity space = spaceRepository
                .findById(spaceId)
                .orElseThrow(() -> new BusinessException("Space not found", HttpStatus.NOT_FOUND));

        MealPollEntity poll = MealPollEntity.builder()
                .space(space)
                .dailyMenu(menu)
                .mealType(mealType)
                .pollDate(pollDate)
                .status(MealPollStatus.OPEN)
                .openedAt(LocalDateTime.now())
                .build();
        poll = pollRepository.save(poll);

        List<MealPollOptionEntity> options = buildOptionsFromMenu(poll, entries, mealType);
        optionRepository.saveAll(options);

        return toPollResponse(poll, null);
    }

    @Transactional
    public MealPollResponse closePoll(UUID spaceId, UUID callerId, LocalDate date, MealType mealType) {
        mealAccessService.requireManageMeals(spaceId, callerId);
        MealPollEntity poll = loadPoll(spaceId, date, mealType);
        if (poll.getStatus() == MealPollStatus.CLOSED) {
            return toPollResponse(poll, null);
        }
        poll.setStatus(MealPollStatus.CLOSED);
        poll.setClosedAt(LocalDateTime.now());
        pollRepository.save(poll);
        return toPollResponse(poll, null);
    }

    @Transactional
    public MealPollDayResponse submitResponses(
            UUID spaceId, UUID callerId, LocalDate date, List<SubmitMealPollSelectionRequest> selections) {
        SpaceMembershipEntity membership = mealAccessService.requireViewMeals(spaceId, callerId);
        if (!mealAccessService.isParticipantScopeOnly(membership)) {
            throw new BusinessException("Only meal participants can submit poll responses", HttpStatus.FORBIDDEN);
        }

        UUID memberId = mealAccessService.resolveOwnMemberId(spaceId, callerId);
        MemberEntity member = memberRepository
                .findById(memberId)
                .orElseThrow(() -> new BusinessException("Member not found", HttpStatus.NOT_FOUND));

        LocalDate pollDate = date != null ? date : LocalDate.now();
        List<MealParticipationEntity> participations =
                participationRepository.findAllNonStoppedBySpaceId(spaceId);

        for (SubmitMealPollSelectionRequest selection : selections) {
            MealPollEntity poll = loadPoll(spaceId, pollDate, selection.getMealType());
            if (poll.getStatus() != MealPollStatus.OPEN) {
                throw new BusinessException(
                        "Poll is closed for " + formatMealType(selection.getMealType()), HttpStatus.BAD_REQUEST);
            }

            MealParticipationEntity participation = participations.stream()
                    .filter(p -> p.getMember().getId().equals(memberId))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("No meal participation found", HttpStatus.FORBIDDEN));

            if (!MealEligibilityEngine.isEligibleForPollAudience(
                    member, participation, pollDate, selection.getMealType())) {
                throw new BusinessException(
                        "You are not eligible for " + formatMealType(selection.getMealType()),
                        HttpStatus.FORBIDDEN);
            }

            MealPollOptionEntity option = optionRepository
                    .findById(selection.getSelectedOptionId())
                    .orElseThrow(() -> new BusinessException("Invalid menu option", HttpStatus.BAD_REQUEST));
            if (!option.getPoll().getId().equals(poll.getId())) {
                throw new BusinessException("Option does not belong to this poll", HttpStatus.BAD_REQUEST);
            }

            MealPollResponseEntity response = responseRepository
                    .findByPollIdAndMemberId(poll.getId(), memberId)
                    .orElse(MealPollResponseEntity.builder()
                            .poll(poll)
                            .member(member)
                            .source(MealPollResponseSource.APP)
                            .build());
            response.setSelectedOption(option);
            response.setRespondedAt(LocalDateTime.now());
            responseRepository.save(response);
        }

        return getPollsForDate(spaceId, callerId, pollDate);
    }

    private List<MealPollOptionEntity> buildOptionsFromMenu(
            MealPollEntity poll, List<DailyMenuEntryEntity> entries, MealType mealType) {
        List<MealPollOptionEntity> options = new ArrayList<>();
        int sortOrder = 1;
        for (DailyMenuEntryEntity entry : entries) {
            options.add(MealPollOptionEntity.builder()
                    .poll(poll)
                    .optionType(MealPollOptionType.MENU_ENTRY)
                    .dailyMenuEntry(entry)
                    .sortOrder(sortOrder++)
                    .label(entry.getLabel())
                    .detail(buildEntryDetail(entry))
                    .build());
        }
        options.add(MealPollOptionEntity.builder()
                .poll(poll)
                .optionType(MealPollOptionType.NOT_AVAILABLE)
                .sortOrder(sortOrder)
                .label("Not available for " + formatMealType(mealType))
                .build());
        return options;
    }

    private String buildEntryDetail(DailyMenuEntryEntity entry) {
        if (entry.getEntryType() == DailyMenuEntryType.COMBO && entry.getCombo() != null) {
            return mealComboItemRepository.findByComboIdWithItems(entry.getCombo().getId()).stream()
                    .map(MealComboItemEntity::getItem)
                    .map(item -> item.getName())
                    .collect(Collectors.joining(", "));
        }
        if (entry.getEntryType() == DailyMenuEntryType.PACKAGE) {
            return dailyMenuPackageItemRepository.findByEntryIdWithItems(entry.getId()).stream()
                    .map(pi -> pi.getItem().getName())
                    .collect(Collectors.joining(", "));
        }
        return null;
    }

    private MealPollEntity loadPoll(UUID spaceId, LocalDate date, MealType mealType) {
        LocalDate pollDate = date != null ? date : LocalDate.now();
        return pollRepository
                .findBySpaceIdAndPollDateAndMealType(spaceId, pollDate, mealType)
                .orElseThrow(() -> new BusinessException("Poll not found for this meal slot", HttpStatus.NOT_FOUND));
    }

    private MealPollResponse toPollResponse(MealPollEntity poll, UUID memberId) {
        List<MealPollOptionEntity> optionEntities = optionRepository.findByPollIdOrderBySortOrderAsc(poll.getId());
        List<MealPollOptionResponse> options = optionEntities.stream()
                .map(this::toOptionResponse)
                .toList();

        UUID selectedOptionId = null;
        if (memberId != null) {
            selectedOptionId = responseRepository
                    .findByPollIdAndMemberId(poll.getId(), memberId)
                    .map(r -> r.getSelectedOption().getId())
                    .orElse(null);
        }

        int responseCount = responseRepository.findByPollId(poll.getId()).size();

        return MealPollResponse.builder()
                .id(poll.getId())
                .pollDate(poll.getPollDate())
                .mealType(poll.getMealType())
                .status(poll.getStatus())
                .dailyMenuId(poll.getDailyMenu().getId())
                .options(options)
                .mySelectedOptionId(selectedOptionId)
                .responseCount(responseCount)
                .build();
    }

    private MealPollOptionResponse toOptionResponse(MealPollOptionEntity option) {
        return MealPollOptionResponse.builder()
                .id(option.getId())
                .optionType(option.getOptionType())
                .sortOrder(option.getSortOrder())
                .label(option.getLabel())
                .detail(option.getDetail())
                .dailyMenuEntryId(
                        option.getDailyMenuEntry() != null ? option.getDailyMenuEntry().getId() : null)
                .build();
    }

    private UUID resolveMemberIdIfParticipant(UUID spaceId, UUID callerId) {
        try {
            return mealAccessService.resolveOwnMemberId(spaceId, callerId);
        } catch (BusinessException ex) {
            return null;
        }
    }

    private String formatMealType(MealType mealType) {
        String name = mealType.name().toLowerCase(Locale.ENGLISH);
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
