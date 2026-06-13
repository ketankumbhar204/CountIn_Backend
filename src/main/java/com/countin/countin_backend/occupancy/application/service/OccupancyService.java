package com.countin.countin_backend.occupancy.application.service;

import com.countin.countin_backend.accommodation.application.service.AccommodationAccessService;
import com.countin.countin_backend.common.exception.BusinessException;
import com.countin.countin_backend.common.exception.ResourceNotFoundException;
import com.countin.countin_backend.common.web.PagedResponse;
import com.countin.countin_backend.member.infrastructure.persistence.entity.MemberEntity;
import com.countin.countin_backend.member.infrastructure.persistence.repository.MemberRepository;
import com.countin.countin_backend.occupancy.api.dto.request.AllocateOccupancyRequest;
import com.countin.countin_backend.occupancy.api.dto.request.TransferOccupancyRequest;
import com.countin.countin_backend.occupancy.api.dto.request.VacateOccupancyRequest;
import com.countin.countin_backend.occupancy.api.dto.response.CurrentOccupancySummaryResponse;
import com.countin.countin_backend.occupancy.api.dto.response.MemberOccupancyListResponse;
import com.countin.countin_backend.occupancy.api.dto.response.OccupancyHistoryEntryResponse;
import com.countin.countin_backend.occupancy.api.dto.response.OccupancyResponse;
import com.countin.countin_backend.occupancy.domain.model.AllocationTargetType;
import com.countin.countin_backend.occupancy.domain.model.MemberOccupancyStatus;
import com.countin.countin_backend.occupancy.domain.model.OccupancyHistoryEvent;
import com.countin.countin_backend.occupancy.domain.model.OccupancyStatus;
import com.countin.countin_backend.occupancy.infrastructure.persistence.entity.OccupancyEntity;
import com.countin.countin_backend.occupancy.infrastructure.persistence.entity.OccupancyHistoryEntity;
import com.countin.countin_backend.occupancy.infrastructure.persistence.repository.OccupancyHistoryRepository;
import com.countin.countin_backend.occupancy.infrastructure.persistence.repository.OccupancyRepository;
import com.countin.countin_backend.space.domain.model.SpaceType;
import com.countin.countin_backend.space.infrastructure.persistence.entity.SpaceEntity;
import com.countin.countin_backend.user.infrastructure.persistence.entity.UserEntity;
import com.countin.countin_backend.user.infrastructure.persistence.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OccupancyService {

    private final OccupancyRepository occupancyRepository;
    private final OccupancyHistoryRepository occupancyHistoryRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final OccupancyAccessService occupancyAccessService;
    private final AccommodationAccessService accommodationAccessService;
    private final OccupancyTargetService occupancyTargetService;
    private final AccommodationStatusSyncService accommodationStatusSyncService;

    @Transactional
    public OccupancyResponse allocate(UUID spaceId, UUID callerId, AllocateOccupancyRequest request) {
        log.info("Allocating member: spaceId={}, memberId={}, callerId={}, targetType={}",
                spaceId, request.getMemberId(), callerId, request.getTargetType());

        occupancyAccessService.assertCanManageOccupancy(spaceId, callerId);
        SpaceEntity space = accommodationAccessService.loadAccommodationSpace(spaceId);
        UserEntity actor = loadUser(callerId);

        MemberEntity member = memberRepository.findByIdAndSpaceIdAndActiveTrue(request.getMemberId(), spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", request.getMemberId()));

        if (occupancyRepository.existsBySpaceIdAndMemberIdAndStatus(
                spaceId, member.getId(), OccupancyStatus.ACTIVE)) {
            throw new BusinessException("Member already has an active occupancy in this space");
        }

        OccupancyTargetService.ResolvedTarget target = occupancyTargetService.resolve(
                spaceId,
                space.getType(),
                request.getTargetType(),
                request.getBedId(),
                request.getRoomId(),
                request.getUnitId());
        assertTargetAvailable(target);

        LocalDateTime now = LocalDateTime.now();
        OccupancyEntity occupancy = OccupancyEntity.builder()
                .space(space)
                .member(member)
                .targetType(target.getTargetType())
                .building(target.getBuilding())
                .floor(target.getFloor())
                .unit(target.getUnit())
                .room(target.getRoom())
                .bed(target.getBed())
                .allocatedAt(now)
                .allocatedBy(actor)
                .expectedCheckoutDate(request.getExpectedCheckoutDate())
                .status(OccupancyStatus.ACTIVE)
                .remarks(request.getRemarks())
                .createdBy(actor)
                .updatedBy(actor)
                .build();

        occupancy = occupancyRepository.save(occupancy);
        accommodationStatusSyncService.markOccupied(target);
        member.setOccupancyStatus(MemberOccupancyStatus.ALLOCATED);
        memberRepository.save(member);

        recordHistory(
                occupancy,
                OccupancyHistoryEvent.ALLOCATED,
                null,
                targetSnapshot(target),
                actor,
                now,
                request.getRemarks());

        return OccupancyResponse.from(occupancy);
    }

    @Transactional
    public OccupancyResponse transfer(
            UUID spaceId, UUID occupancyId, UUID callerId, TransferOccupancyRequest request) {
        log.info("Transferring occupancy: spaceId={}, occupancyId={}, callerId={}", spaceId, occupancyId, callerId);

        occupancyAccessService.assertCanManageOccupancy(spaceId, callerId);
        SpaceEntity space = accommodationAccessService.loadAccommodationSpace(spaceId);
        UserEntity actor = loadUser(callerId);

        OccupancyEntity current = loadActiveOccupancy(spaceId, occupancyId);
        TargetSnapshot fromTarget = targetSnapshot(current);

        OccupancyTargetService.ResolvedTarget newTarget = occupancyTargetService.resolve(
                spaceId,
                space.getType(),
                request.getTargetType(),
                request.getBedId(),
                request.getRoomId(),
                request.getUnitId());
        assertTargetAvailable(newTarget);

        LocalDateTime now = LocalDateTime.now();
        closeOccupancy(current, actor, now, request.getRemarks());
        accommodationStatusSyncService.releaseTarget(
                fromTarget.targetType(),
                fromTarget.bedId(),
                fromTarget.roomId(),
                fromTarget.unitId());

        OccupancyEntity next = OccupancyEntity.builder()
                .space(space)
                .member(current.getMember())
                .targetType(newTarget.getTargetType())
                .building(newTarget.getBuilding())
                .floor(newTarget.getFloor())
                .unit(newTarget.getUnit())
                .room(newTarget.getRoom())
                .bed(newTarget.getBed())
                .allocatedAt(now)
                .allocatedBy(actor)
                .status(OccupancyStatus.ACTIVE)
                .remarks(request.getRemarks())
                .createdBy(actor)
                .updatedBy(actor)
                .build();

        next = occupancyRepository.save(next);
        accommodationStatusSyncService.markOccupied(newTarget);

        recordHistory(
                next,
                OccupancyHistoryEvent.TRANSFERRED,
                fromTarget,
                targetSnapshot(newTarget),
                actor,
                now,
                request.getRemarks());

        return OccupancyResponse.from(next);
    }

    @Transactional
    public OccupancyResponse vacate(
            UUID spaceId, UUID occupancyId, UUID callerId, VacateOccupancyRequest request) {
        log.info("Vacating occupancy: spaceId={}, occupancyId={}, callerId={}", spaceId, occupancyId, callerId);

        occupancyAccessService.assertCanManageOccupancy(spaceId, callerId);
        accommodationAccessService.loadAccommodationSpace(spaceId);
        UserEntity actor = loadUser(callerId);

        OccupancyEntity occupancy = loadActiveOccupancy(spaceId, occupancyId);
        TargetSnapshot fromTarget = targetSnapshot(occupancy);
        LocalDateTime now = LocalDateTime.now();

        closeOccupancy(occupancy, actor, now, request.getRemarks());
        accommodationStatusSyncService.releaseTarget(
                fromTarget.targetType(),
                fromTarget.bedId(),
                fromTarget.roomId(),
                fromTarget.unitId());

        MemberEntity member = occupancy.getMember();
        member.setOccupancyStatus(MemberOccupancyStatus.VACATED);
        memberRepository.save(member);

        recordHistory(
                occupancy,
                OccupancyHistoryEvent.VACATED,
                fromTarget,
                null,
                actor,
                now,
                request.getRemarks());

        return OccupancyResponse.from(occupancy);
    }

    @Transactional(readOnly = true)
    public OccupancyResponse getOccupancy(UUID spaceId, UUID occupancyId, UUID callerId) {
        OccupancyEntity occupancy = occupancyRepository.findByIdAndSpaceId(occupancyId, spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Occupancy", "id", occupancyId));

        occupancyAccessService.assertCanViewMemberOccupancy(
                spaceId, occupancy.getMember().getId(), callerId, occupancy.getMember());
        return OccupancyResponse.from(occupancy);
    }

    @Transactional(readOnly = true)
    public PagedResponse<OccupancyResponse> listSpaceOccupancies(
            UUID spaceId,
            UUID callerId,
            OccupancyStatus status,
            UUID memberId,
            UUID buildingId,
            UUID floorId,
            UUID unitId,
            UUID roomId,
            UUID bedId,
            AllocationTargetType targetType,
            Pageable pageable) {
        occupancyAccessService.assertCanViewSpaceOccupancies(spaceId, callerId);

        Page<OccupancyEntity> page = occupancyRepository.search(
                spaceId, status, memberId, buildingId, floorId, unitId, roomId, bedId, targetType, pageable);
        return PagedResponse.from(page.map(OccupancyResponse::from));
    }

    @Transactional(readOnly = true)
    public MemberOccupancyListResponse getMemberOccupancies(UUID spaceId, UUID memberId, UUID callerId) {
        MemberEntity member = memberRepository.findByIdAndSpaceIdAndActiveTrue(memberId, spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", memberId));

        occupancyAccessService.assertCanViewMemberOccupancy(spaceId, memberId, callerId, member);

        List<OccupancyEntity> occupancies =
                occupancyRepository.findAllBySpaceIdAndMemberIdOrderByAllocatedAtDesc(spaceId, memberId);
        List<OccupancyHistoryEntryResponse> history = occupancyHistoryRepository
                .findBySpaceIdAndMemberIdOrderByPerformedAtDesc(spaceId, memberId)
                .stream()
                .map(OccupancyHistoryEntryResponse::from)
                .toList();

        Optional<OccupancyEntity> current = occupancies.stream()
                .filter(o -> o.getStatus() == OccupancyStatus.ACTIVE)
                .findFirst();

        return MemberOccupancyListResponse.builder()
                .currentOccupancy(current.map(OccupancyResponse::from).orElse(null))
                .occupancies(occupancies.stream().map(OccupancyResponse::from).toList())
                .history(history)
                .build();
    }

    @Transactional(readOnly = true)
    public Optional<CurrentOccupancySummaryResponse> findCurrentOccupancySummary(UUID spaceId, UUID memberId) {
        return occupancyRepository
                .findActiveBySpaceIdAndMemberId(spaceId, memberId)
                .map(occupancy -> CurrentOccupancySummaryResponse.builder()
                        .targetType(occupancy.getTargetType())
                        .buildingId(occupancy.getBuilding().getId())
                        .buildingName(occupancy.getBuilding().getName())
                        .floorId(occupancy.getFloor() != null ? occupancy.getFloor().getId() : null)
                        .floorName(occupancy.getFloor() != null ? occupancy.getFloor().getName() : null)
                        .unitId(occupancy.getUnit() != null ? occupancy.getUnit().getId() : null)
                        .unitName(occupancy.getUnit() != null ? occupancy.getUnit().getName() : null)
                        .roomId(occupancy.getRoom() != null ? occupancy.getRoom().getId() : null)
                        .roomName(occupancy.getRoom() != null ? occupancy.getRoom().getName() : null)
                        .bedId(occupancy.getBed() != null ? occupancy.getBed().getId() : null)
                        .bedName(occupancy.getBed() != null ? occupancy.getBed().getName() : null)
                        .build());
    }

    private OccupancyEntity loadActiveOccupancy(UUID spaceId, UUID occupancyId) {
        OccupancyEntity occupancy = occupancyRepository.findByIdAndSpaceId(occupancyId, spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Occupancy", "id", occupancyId));
        if (occupancy.getStatus() != OccupancyStatus.ACTIVE) {
            throw new BusinessException("Occupancy is not active");
        }
        return occupancy;
    }

    private void closeOccupancy(OccupancyEntity occupancy, UserEntity actor, LocalDateTime now, String remarks) {
        occupancy.setStatus(OccupancyStatus.VACATED);
        occupancy.setVacatedAt(now);
        occupancy.setVacatedBy(actor);
        occupancy.setUpdatedBy(actor);
        if (remarks != null && !remarks.isBlank()) {
            occupancy.setRemarks(remarks);
        }
        occupancyRepository.save(occupancy);
    }

    private void assertTargetAvailable(OccupancyTargetService.ResolvedTarget target) {
        switch (target.getTargetType()) {
            case BED -> {
                if (occupancyRepository.existsByBedIdAndStatus(target.getBed().getId(), OccupancyStatus.ACTIVE)) {
                    throw new BusinessException("Bed is already occupied");
                }
            }
            case ROOM -> {
                if (occupancyRepository.existsByRoomIdAndStatus(target.getRoom().getId(), OccupancyStatus.ACTIVE)) {
                    throw new BusinessException("Room is already occupied");
                }
            }
            case UNIT -> {
                if (occupancyRepository.existsByUnitIdAndStatus(target.getUnit().getId(), OccupancyStatus.ACTIVE)) {
                    throw new BusinessException("Unit is already occupied");
                }
            }
        }
    }

    private void recordHistory(
            OccupancyEntity occupancy,
            OccupancyHistoryEvent eventType,
            TargetSnapshot from,
            TargetSnapshot to,
            UserEntity actor,
            LocalDateTime performedAt,
            String remarks) {
        OccupancyHistoryEntity history = OccupancyHistoryEntity.builder()
                .occupancy(occupancy)
                .space(occupancy.getSpace())
                .member(occupancy.getMember())
                .eventType(eventType)
                .fromTargetType(from != null ? from.targetType() : null)
                .fromBuildingId(from != null ? from.buildingId() : null)
                .fromFloorId(from != null ? from.floorId() : null)
                .fromUnitId(from != null ? from.unitId() : null)
                .fromRoomId(from != null ? from.roomId() : null)
                .fromBedId(from != null ? from.bedId() : null)
                .toTargetType(to != null ? to.targetType() : null)
                .toBuildingId(to != null ? to.buildingId() : null)
                .toFloorId(to != null ? to.floorId() : null)
                .toUnitId(to != null ? to.unitId() : null)
                .toRoomId(to != null ? to.roomId() : null)
                .toBedId(to != null ? to.bedId() : null)
                .performedBy(actor)
                .performedAt(performedAt)
                .remarks(remarks)
                .build();
        occupancyHistoryRepository.save(history);
    }

    private TargetSnapshot targetSnapshot(OccupancyTargetService.ResolvedTarget target) {
        return new TargetSnapshot(
                target.getTargetType(),
                target.getBuilding().getId(),
                target.getFloor() != null ? target.getFloor().getId() : null,
                target.getUnit() != null ? target.getUnit().getId() : null,
                target.getRoom() != null ? target.getRoom().getId() : null,
                target.getBed() != null ? target.getBed().getId() : null);
    }

    private TargetSnapshot targetSnapshot(OccupancyEntity occupancy) {
        return new TargetSnapshot(
                occupancy.getTargetType(),
                occupancy.getBuilding().getId(),
                occupancy.getFloor() != null ? occupancy.getFloor().getId() : null,
                occupancy.getUnit() != null ? occupancy.getUnit().getId() : null,
                occupancy.getRoom() != null ? occupancy.getRoom().getId() : null,
                occupancy.getBed() != null ? occupancy.getBed().getId() : null);
    }

    private UserEntity loadUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private record TargetSnapshot(
            AllocationTargetType targetType,
            UUID buildingId,
            UUID floorId,
            UUID unitId,
            UUID roomId,
            UUID bedId) {}
}
