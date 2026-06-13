package com.countin.countin_backend.occupancy.application.service;

import com.countin.countin_backend.accommodation.domain.model.AccommodationStatus;
import com.countin.countin_backend.accommodation.infrastructure.persistence.entity.BedEntity;
import com.countin.countin_backend.accommodation.infrastructure.persistence.entity.RoomEntity;
import com.countin.countin_backend.accommodation.infrastructure.persistence.entity.UnitEntity;
import com.countin.countin_backend.accommodation.infrastructure.persistence.repository.BedRepository;
import com.countin.countin_backend.accommodation.infrastructure.persistence.repository.RoomRepository;
import com.countin.countin_backend.accommodation.infrastructure.persistence.repository.UnitRepository;
import com.countin.countin_backend.occupancy.domain.model.AllocationTargetType;
import com.countin.countin_backend.occupancy.domain.model.OccupancyStatus;
import com.countin.countin_backend.occupancy.infrastructure.persistence.repository.OccupancyRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccommodationStatusSyncService {

    private final OccupancyRepository occupancyRepository;
    private final BedRepository bedRepository;
    private final RoomRepository roomRepository;
    private final UnitRepository unitRepository;

    @Transactional
    public void markOccupied(OccupancyTargetService.ResolvedTarget target) {
        switch (target.getTargetType()) {
            case BED -> {
                BedEntity bed = target.getBed();
                bed.setStatus(AccommodationStatus.OCCUPIED);
                bedRepository.save(bed);
                syncRoomStatus(target.getRoom().getId());
            }
            case ROOM -> {
                RoomEntity room = target.getRoom();
                room.setStatus(AccommodationStatus.OCCUPIED);
                roomRepository.save(room);
                if (target.getUnit() != null) {
                    syncUnitStatus(target.getUnit().getId());
                }
            }
            case UNIT -> {
                UnitEntity unit = target.getUnit();
                unit.setStatus(AccommodationStatus.OCCUPIED);
                unitRepository.save(unit);
            }
        }
    }

    @Transactional
    public void releaseTarget(AllocationTargetType targetType, UUID bedId, UUID roomId, UUID unitId) {
        switch (targetType) {
            case BED -> {
                if (bedId != null) {
                    bedRepository.findById(bedId).ifPresent(bed -> {
                        if (!occupancyRepository.existsByBedIdAndStatus(bedId, OccupancyStatus.ACTIVE)) {
                            bed.setStatus(AccommodationStatus.AVAILABLE);
                            bedRepository.save(bed);
                        }
                    });
                }
                if (roomId != null) {
                    syncRoomStatus(roomId);
                }
            }
            case ROOM -> {
                if (roomId != null) {
                    roomRepository.findById(roomId).ifPresent(room -> {
                        if (!occupancyRepository.existsByRoomIdAndStatus(roomId, OccupancyStatus.ACTIVE)) {
                            room.setStatus(AccommodationStatus.AVAILABLE);
                            roomRepository.save(room);
                        }
                    });
                }
                if (unitId != null) {
                    syncUnitStatus(unitId);
                }
            }
            case UNIT -> {
                if (unitId != null) {
                    unitRepository.findById(unitId).ifPresent(unit -> {
                        if (!occupancyRepository.existsByUnitIdAndStatus(unitId, OccupancyStatus.ACTIVE)) {
                            unit.setStatus(AccommodationStatus.AVAILABLE);
                            unitRepository.save(unit);
                        }
                    });
                }
            }
        }
    }

    private void syncRoomStatus(UUID roomId) {
        roomRepository.findById(roomId).ifPresent(room -> {
            boolean occupied = occupancyRepository.existsByRoomIdAndStatus(roomId, OccupancyStatus.ACTIVE)
                    || bedRepository.findActiveByRoomId(roomId).stream()
                            .anyMatch(bed -> occupancyRepository.existsByBedIdAndStatus(
                                    bed.getId(), OccupancyStatus.ACTIVE));
            room.setStatus(occupied ? AccommodationStatus.OCCUPIED : AccommodationStatus.AVAILABLE);
            roomRepository.save(room);

            if (room.getUnit() != null) {
                syncUnitStatus(room.getUnit().getId());
            }
        });
    }

    private void syncUnitStatus(UUID unitId) {
        unitRepository.findById(unitId).ifPresent(unit -> {
            boolean occupied = occupancyRepository.existsByUnitIdAndStatus(unitId, OccupancyStatus.ACTIVE)
                    || roomRepository.findActiveByUnitId(unitId).stream()
                            .anyMatch(room -> occupancyRepository.existsByRoomIdAndStatus(
                                            room.getId(), OccupancyStatus.ACTIVE)
                                    || bedRepository.findActiveByRoomId(room.getId()).stream()
                                            .anyMatch(bed -> occupancyRepository.existsByBedIdAndStatus(
                                                    bed.getId(), OccupancyStatus.ACTIVE)));
            unit.setStatus(occupied ? AccommodationStatus.OCCUPIED : AccommodationStatus.AVAILABLE);
            unitRepository.save(unit);
        });
    }
}
