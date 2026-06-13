package com.countin.countin_backend.accommodation.infrastructure.persistence.repository;

import com.countin.countin_backend.accommodation.domain.model.AccommodationStatus;
import com.countin.countin_backend.accommodation.infrastructure.persistence.entity.BuildingEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccommodationSummaryRepository extends JpaRepository<BuildingEntity, UUID> {

    @Query("""
            SELECT COUNT(f) FROM FloorEntity f
            WHERE f.building.id = :buildingId AND f.isActive = true
            """)
    long countActiveFloors(@Param("buildingId") UUID buildingId);

    @Query("""
            SELECT COUNT(u) FROM UnitEntity u
            WHERE u.building.id = :buildingId AND u.isActive = true
            """)
    long countActiveUnits(@Param("buildingId") UUID buildingId);

    @Query("""
            SELECT COUNT(u) FROM UnitEntity u
            WHERE u.building.id = :buildingId AND u.isActive = true AND u.synthetic = false
            """)
    long countVisibleActiveUnits(@Param("buildingId") UUID buildingId);

    @Query("""
            SELECT COUNT(u) FROM UnitEntity u
            WHERE u.building.id = :buildingId AND u.isActive = true AND u.synthetic = true
            """)
    long countSyntheticActiveUnits(@Param("buildingId") UUID buildingId);

    @Query("""
            SELECT COUNT(r) FROM RoomEntity r
            WHERE r.isActive = true
              AND (
                  (r.floor IS NOT NULL AND r.floor.building.id = :buildingId)
                  OR (r.unit IS NOT NULL AND r.unit.building.id = :buildingId)
              )
            """)
    long countActiveRooms(@Param("buildingId") UUID buildingId);

    @Query("""
            SELECT COUNT(b) FROM BedEntity b
            WHERE b.isActive = true
              AND b.room.isActive = true
              AND (
                  (b.room.floor IS NOT NULL AND b.room.floor.building.id = :buildingId)
                  OR (b.room.unit IS NOT NULL AND b.room.unit.building.id = :buildingId)
              )
            """)
    long countActiveBeds(@Param("buildingId") UUID buildingId);

    @Query("""
            SELECT r.status, COUNT(r) FROM RoomEntity r
            WHERE r.isActive = true
              AND (
                  (r.floor IS NOT NULL AND r.floor.building.id = :buildingId)
                  OR (r.unit IS NOT NULL AND r.unit.building.id = :buildingId)
              )
            GROUP BY r.status
            """)
    List<Object[]> countRoomStatuses(@Param("buildingId") UUID buildingId);

    @Query("""
            SELECT b.status, COUNT(b) FROM BedEntity b
            WHERE b.isActive = true
              AND b.room.isActive = true
              AND (
                  (b.room.floor IS NOT NULL AND b.room.floor.building.id = :buildingId)
                  OR (b.room.unit IS NOT NULL AND b.room.unit.building.id = :buildingId)
              )
            GROUP BY b.status
            """)
    List<Object[]> countBedStatuses(@Param("buildingId") UUID buildingId);

    @Query("""
            SELECT u.status, COUNT(u) FROM UnitEntity u
            WHERE u.building.id = :buildingId AND u.isActive = true
            GROUP BY u.status
            """)
    List<Object[]> countUnitStatuses(@Param("buildingId") UUID buildingId);

    @Query("""
            SELECT COUNT(b) FROM BedEntity b
            WHERE b.isActive = true AND b.room.isActive = true AND b.status = :status
              AND (
                  (b.room.floor IS NOT NULL AND b.room.floor.building.id = :buildingId)
                  OR (b.room.unit IS NOT NULL AND b.room.unit.building.id = :buildingId)
              )
            """)
    long countBedsByStatus(
            @Param("buildingId") UUID buildingId, @Param("status") AccommodationStatus status);

    @Query("""
            SELECT COUNT(r) FROM RoomEntity r
            WHERE r.isActive = true AND r.status = :status
              AND (
                  (r.floor IS NOT NULL AND r.floor.building.id = :buildingId)
                  OR (r.unit IS NOT NULL AND r.unit.building.id = :buildingId)
              )
            """)
    long countRoomsByStatus(
            @Param("buildingId") UUID buildingId, @Param("status") AccommodationStatus status);

    @Query("""
            SELECT COUNT(u) FROM UnitEntity u
            WHERE u.building.id = :buildingId AND u.isActive = true AND u.status = :status
            """)
    long countUnitsByStatus(
            @Param("buildingId") UUID buildingId, @Param("status") AccommodationStatus status);
}
