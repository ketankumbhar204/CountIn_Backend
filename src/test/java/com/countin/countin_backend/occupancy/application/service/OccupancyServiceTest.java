package com.countin.countin_backend.occupancy.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.countin.countin_backend.accommodation.application.service.AccommodationAccessService;
import com.countin.countin_backend.accommodation.domain.model.AccommodationStatus;
import com.countin.countin_backend.accommodation.infrastructure.persistence.entity.BedEntity;
import com.countin.countin_backend.accommodation.infrastructure.persistence.entity.BuildingEntity;
import com.countin.countin_backend.accommodation.infrastructure.persistence.entity.FloorEntity;
import com.countin.countin_backend.accommodation.infrastructure.persistence.entity.RoomEntity;
import com.countin.countin_backend.common.exception.BusinessException;
import com.countin.countin_backend.member.infrastructure.persistence.entity.MemberEntity;
import com.countin.countin_backend.occupancy.api.dto.request.AllocateOccupancyRequest;
import com.countin.countin_backend.occupancy.api.dto.request.VacateOccupancyRequest;
import com.countin.countin_backend.occupancy.domain.model.AllocationTargetType;
import com.countin.countin_backend.occupancy.domain.model.MemberOccupancyStatus;
import com.countin.countin_backend.occupancy.domain.model.OccupancyStatus;
import com.countin.countin_backend.occupancy.infrastructure.persistence.entity.OccupancyEntity;
import com.countin.countin_backend.occupancy.infrastructure.persistence.repository.OccupancyHistoryRepository;
import com.countin.countin_backend.occupancy.infrastructure.persistence.repository.OccupancyRepository;
import com.countin.countin_backend.space.domain.model.SpaceType;
import com.countin.countin_backend.space.infrastructure.persistence.entity.SpaceEntity;
import com.countin.countin_backend.user.infrastructure.persistence.entity.UserEntity;
import com.countin.countin_backend.user.infrastructure.persistence.repository.UserRepository;
import com.countin.countin_backend.member.infrastructure.persistence.repository.MemberRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OccupancyServiceTest {

    @Mock
    private OccupancyRepository occupancyRepository;

    @Mock
    private OccupancyHistoryRepository occupancyHistoryRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OccupancyAccessService occupancyAccessService;

    @Mock
    private AccommodationAccessService accommodationAccessService;

    @Mock
    private OccupancyTargetService occupancyTargetService;

    @Mock
    private AccommodationStatusSyncService accommodationStatusSyncService;

    @InjectMocks
    private OccupancyService occupancyService;

    private UUID spaceId;
    private UUID callerId;
    private UUID memberId;
    private UUID bedId;

    @BeforeEach
    void setUp() {
        spaceId = UUID.randomUUID();
        callerId = UUID.randomUUID();
        memberId = UUID.randomUUID();
        bedId = UUID.randomUUID();
    }

    @Test
    void allocate_whenMemberAlreadyAllocated_throws() {
        AllocateOccupancyRequest request = new AllocateOccupancyRequest();
        request.setMemberId(memberId);
        request.setTargetType(AllocationTargetType.BED);
        request.setBedId(bedId);

        SpaceEntity space = SpaceEntity.builder().type(SpaceType.PG).isActive(true).build();
        space.setId(spaceId);
        MemberEntity member = MemberEntity.builder().fullName("Ravi").mobileNumber("9999999999").build();
        member.setId(memberId);

        when(accommodationAccessService.loadAccommodationSpace(spaceId)).thenReturn(space);
        when(userRepository.findById(callerId)).thenReturn(Optional.of(UserEntity.builder().build()));
        when(memberRepository.findByIdAndSpaceIdAndActiveTrue(memberId, spaceId)).thenReturn(Optional.of(member));
        when(occupancyRepository.existsBySpaceIdAndMemberIdAndStatus(spaceId, memberId, OccupancyStatus.ACTIVE))
                .thenReturn(true);

        assertThatThrownBy(() -> occupancyService.allocate(spaceId, callerId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already has an active occupancy");

        verify(occupancyRepository, never()).save(any());
    }

    @Test
    void allocate_whenTargetAvailable_createsOccupancyAndUpdatesMember() {
        AllocateOccupancyRequest request = new AllocateOccupancyRequest();
        request.setMemberId(memberId);
        request.setTargetType(AllocationTargetType.BED);
        request.setBedId(bedId);

        SpaceEntity space = SpaceEntity.builder().type(SpaceType.PG).isActive(true).build();
        space.setId(spaceId);
        MemberEntity member = MemberEntity.builder().fullName("Ravi").mobileNumber("9999999999").space(space).build();
        member.setId(memberId);

        BuildingEntity building = BuildingEntity.builder().space(space).name("Block A").code("A").build();
        building.setId(UUID.randomUUID());
        FloorEntity floor = FloorEntity.builder().building(building).name("Floor 1").floorNumber(1).build();
        floor.setId(UUID.randomUUID());
        RoomEntity room = RoomEntity.builder()
                .floor(floor)
                .name("Room 101")
                .roomNumber("101")
                .status(AccommodationStatus.AVAILABLE)
                .build();
        room.setId(UUID.randomUUID());
        BedEntity bed = BedEntity.builder()
                .room(room)
                .name("Bed A")
                .bedNumber("A")
                .status(AccommodationStatus.AVAILABLE)
                .build();
        bed.setId(bedId);

        OccupancyTargetService.ResolvedTarget target = OccupancyTargetService.ResolvedTarget.builder()
                .targetType(AllocationTargetType.BED)
                .building(building)
                .floor(floor)
                .room(room)
                .bed(bed)
                .build();

        UserEntity actor = UserEntity.builder().build();
        actor.setId(callerId);

        when(accommodationAccessService.loadAccommodationSpace(spaceId)).thenReturn(space);
        when(userRepository.findById(callerId)).thenReturn(Optional.of(actor));
        when(memberRepository.findByIdAndSpaceIdAndActiveTrue(memberId, spaceId)).thenReturn(Optional.of(member));
        when(occupancyRepository.existsBySpaceIdAndMemberIdAndStatus(spaceId, memberId, OccupancyStatus.ACTIVE))
                .thenReturn(false);
        when(occupancyTargetService.resolve(
                        eq(spaceId),
                        eq(SpaceType.PG),
                        eq(AllocationTargetType.BED),
                        eq(bedId),
                        eq(null),
                        eq(null)))
                .thenReturn(target);
        when(occupancyRepository.existsByBedIdAndStatus(bedId, OccupancyStatus.ACTIVE)).thenReturn(false);
        when(occupancyRepository.save(any())).thenAnswer(invocation -> {
            OccupancyEntity saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        occupancyService.allocate(spaceId, callerId, request);

        verify(accommodationStatusSyncService).markOccupied(target);
        verify(memberRepository).save(member);
        org.assertj.core.api.Assertions.assertThat(member.getOccupancyStatus()).isEqualTo(MemberOccupancyStatus.ALLOCATED);
        verify(occupancyHistoryRepository).save(any());
    }

    @Test
    void vacate_whenActive_releasesTargetAndMarksMemberVacated() {
        UUID occupancyId = UUID.randomUUID();
        VacateOccupancyRequest request = new VacateOccupancyRequest();
        request.setRemarks("Checkout completed");

        SpaceEntity space = SpaceEntity.builder().type(SpaceType.PG).isActive(true).build();
        space.setId(spaceId);
        MemberEntity member = MemberEntity.builder().fullName("Ravi").mobileNumber("9999999999").space(space).build();
        member.setId(memberId);

        BuildingEntity building = BuildingEntity.builder().space(space).name("Block A").code("A").build();
        building.setId(UUID.randomUUID());
        BedEntity bed = BedEntity.builder().name("Bed A").bedNumber("A").status(AccommodationStatus.OCCUPIED).build();
        bed.setId(bedId);
        RoomEntity room = RoomEntity.builder().name("Room 101").roomNumber("101").build();
        room.setId(UUID.randomUUID());
        bed.setRoom(room);

        UserEntity actor = UserEntity.builder().build();
        actor.setId(callerId);

        OccupancyEntity occupancy = OccupancyEntity.builder()
                .space(space)
                .member(member)
                .targetType(AllocationTargetType.BED)
                .building(building)
                .room(room)
                .bed(bed)
                .allocatedBy(actor)
                .status(OccupancyStatus.ACTIVE)
                .build();
        occupancy.setId(occupancyId);

        when(accommodationAccessService.loadAccommodationSpace(spaceId)).thenReturn(space);
        when(userRepository.findById(callerId)).thenReturn(Optional.of(actor));
        when(occupancyRepository.findByIdAndSpaceId(occupancyId, spaceId)).thenReturn(Optional.of(occupancy));

        occupancyService.vacate(spaceId, occupancyId, callerId, request);

        verify(accommodationStatusSyncService)
                .releaseTarget(AllocationTargetType.BED, bedId, room.getId(), null);
        org.assertj.core.api.Assertions.assertThat(member.getOccupancyStatus()).isEqualTo(MemberOccupancyStatus.VACATED);
        org.assertj.core.api.Assertions.assertThat(occupancy.getStatus()).isEqualTo(OccupancyStatus.VACATED);
    }
}
