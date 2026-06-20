package com.countin.countin_backend.dashboard.application.support;

import com.countin.countin_backend.occupancy.infrastructure.persistence.entity.OccupancyEntity;
import java.math.BigDecimal;

public final class OccupancyBillingCalculator {

    private OccupancyBillingCalculator() {}

    public static BigDecimal computeMonthlyExpected(OccupancyEntity occupancy) {
        if (occupancy.getRentSnapshot() == null) {
            return null;
        }

        if (occupancy.isFoodIncludedInRent()) {
            return occupancy.getRentSnapshot();
        }

        if (!occupancy.isFoodEnabled()) {
            return occupancy.getRentSnapshot();
        }

        BigDecimal food = occupancy.getFoodChargeSnapshot() != null
                ? occupancy.getFoodChargeSnapshot()
                : BigDecimal.ZERO;
        return occupancy.getRentSnapshot().add(food);
    }
}
