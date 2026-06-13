package com.countin.countin_backend.occupancy.application.service;

import com.countin.countin_backend.accommodation.domain.model.AccommodationStatus;
import com.countin.countin_backend.accommodation.infrastructure.persistence.entity.BuildingEntity;
import com.countin.countin_backend.accommodation.infrastructure.persistence.entity.BedEntity;
import com.countin.countin_backend.accommodation.infrastructure.persistence.entity.FloorEntity;
import com.countin.countin_backend.accommodation.infrastructure.persistence.entity.RoomEntity;
import com.countin.countin_backend.accommodation.infrastructure.persistence.entity.UnitEntity;
import com.countin.countin_backend.accommodation.infrastructure.persistence.repository.BedRepository;
import com.countin.countin_backend.accommodation.infrastructure.persistence.repository.RoomRepository;
import com.countin.countin_backend.accommodation.infrastructure.persistence.repository.UnitRepository;
import com.countin.countin_backend.common.exception.BusinessException;
import com.countin.countin_backend.common.exception.ResourceNotFoundException;
import com.countin.countin_backend.occupancy.domain.model.AllocationTargetType;
import com.countin.countin_backend.space.domain.model.SpaceType;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OccupancyTargetService {

    private final BedRepository bedRepository;
    private final RoomRepository roomRepository;
    private final UnitRepository unitRepository;

    public ResolvedTarget resolve(UUID spaceId, SpaceType spaceType, AllocationTargetType targetType, UUID bedId,
            UUID roomId, UUID unitId) {
        assertTargetTypeAllowed(spaceType, targetType);
        return switch (targetType) {
            case BED -> resolveBed(spaceId, requireId(bedId, "bedId"));
            case ROOM -> resolveRoom(spaceId, requireId(roomId, "roomId"));
            case UNIT -> resolveUnit(spaceId, requireId(unitId, "unitId"));
        };
    }

    public void assertTargetTypeAllowed(SpaceType spaceType, AllocationTargetType targetType) {
        switch (spaceType) {
            case PG, HOSTEL -> {
                if (targetType != AllocationTargetType.BED) {
                    throw new BusinessException("Only BED allocation is allowed for PG and Hostel spaces");
                }
            }
            case CO_LIVING -> {
                if (targetType != AllocationTargetType.BED && targetType != AllocationTargetType.ROOM) {
                    throw new BusinessException("Only BED or ROOM allocation is allowed for Co-Living spaces");
                }
            }
            case RENTAL -> {
                if (targetType != AllocationTargetType.UNIT) {
                    throw new BusinessException("Only UNIT allocation is allowed for Rental spaces");
                }
            }
            case MESS -> throw new BusinessException("Accommodation is not applicable for Mess spaces");
        }
    }

    private ResolvedTarget resolveBed(UUID spaceId, UUID bedId) {
        BedEntity bed = bedRepository.findByIdAndSpaceId(bedId, spaceId)
                .orElseThrow(() -> ResourceNotFoundException.notInSpace("Bed", bedId));
        if (!bed.isActive()) {
            throw new BusinessException("Bed is not active");
        }
        assertAllocatable(bed.getStatus(), "Bed");

        RoomEntity room = bed.getRoom();
        if (room == null || !room.isActive()) {
            throw new BusinessException("Bed room is not active");
        }

        BuildingEntity building;
        FloorEntity floor = null;
        UnitEntity unit = null;

        if (room.getFloor() != null) {
            floor = room.getFloor();
            building = floor.getBuilding();
        } else if (room.getUnit() != null) {
            unit = room.getUnit();
            floor = unit.getFloor();
            building = unit.getBuilding();
        } else {
            throw new BusinessException("Bed is not linked to a valid accommodation structure");
        }

        assertBuildingInSpace(building, spaceId);

        return ResolvedTarget.builder()
                .targetType(AllocationTargetType.BED)
                .building(building)
                .floor(floor)
                .unit(unit)
                .room(room)
                .bed(bed)
                .build();
    }

    private ResolvedTarget resolveRoom(UUID spaceId, UUID roomId) {
        RoomEntity room = roomRepository.findByIdAndSpaceId(roomId, spaceId)
                .orElseThrow(() -> ResourceNotFoundException.notInSpace("Room", roomId));
        if (!room.isActive()) {
            throw new BusinessException("Room is not active");
        }
        assertAllocatable(room.getStatus(), "Room");

        BuildingEntity building;
        FloorEntity floor = null;
        UnitEntity unit = null;

        if (room.getFloor() != null) {
            floor = room.getFloor();
            building = floor.getBuilding();
        } else if (room.getUnit() != null) {
            unit = room.getUnit();
            floor = unit.getFloor();
            building = unit.getBuilding();
        } else {
            throw new BusinessException("Room is not linked to a valid accommodation structure");
        }

        assertBuildingInSpace(building, spaceId);

        return ResolvedTarget.builder()
                .targetType(AllocationTargetType.ROOM)
                .building(building)
                .floor(floor)
                .unit(unit)
                .room(room)
                .build();
    }

    private ResolvedTarget resolveUnit(UUID spaceId, UUID unitId) {
        UnitEntity unit = unitRepository.findByIdAndSpaceId(unitId, spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Unit", "id", unitId));
        if (!unit.isActive()) {
            throw new BusinessException("Unit is not active");
        }
        assertAllocatable(unit.getStatus(), "Unit");
        assertBuildingInSpace(unit.getBuilding(), spaceId);

        return ResolvedTarget.builder()
                .targetType(AllocationTargetType.UNIT)
                .building(unit.getBuilding())
                .floor(unit.getFloor())
                .unit(unit)
                .build();
    }

    private void assertAllocatable(AccommodationStatus status, String label) {
        if (status != AccommodationStatus.AVAILABLE) {
            throw new BusinessException(label + " is not available for allocation");
        }
    }

    private void assertBuildingInSpace(BuildingEntity building, UUID spaceId) {
        if (building == null || !building.isActive() || !building.getSpace().getId().equals(spaceId)) {
            throw new BusinessException("Target does not belong to this space");
        }
    }

    private UUID requireId(UUID id, String fieldName) {
        if (id == null) {
            throw new BusinessException(fieldName + " is required for this target type");
        }
        return id;
    }

    @Getter
    @Builder
    public static class ResolvedTarget {
        private final AllocationTargetType targetType;
        private final BuildingEntity building;
        private final FloorEntity floor;
        private final UnitEntity unit;
        private final RoomEntity room;
        private final BedEntity bed;
    }
}
