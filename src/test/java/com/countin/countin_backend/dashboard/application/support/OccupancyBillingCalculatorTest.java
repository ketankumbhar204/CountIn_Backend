package com.countin.countin_backend.dashboard.application.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.countin.countin_backend.occupancy.infrastructure.persistence.entity.OccupancyEntity;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class OccupancyBillingCalculatorTest {

    @Test
    void computeMonthlyExpected_returnsNullWhenRentMissing() {
        OccupancyEntity occupancy = OccupancyEntity.builder().build();

        assertThat(OccupancyBillingCalculator.computeMonthlyExpected(occupancy)).isNull();
    }

    @Test
    void computeMonthlyExpected_returnsRentWhenFoodIncludedInRent() {
        OccupancyEntity occupancy = OccupancyEntity.builder()
                .rentSnapshot(new BigDecimal("8000"))
                .foodIncludedInRent(true)
                .foodEnabled(true)
                .foodChargeSnapshot(new BigDecimal("2500"))
                .build();

        assertThat(OccupancyBillingCalculator.computeMonthlyExpected(occupancy))
                .isEqualByComparingTo(new BigDecimal("8000"));
    }

    @Test
    void computeMonthlyExpected_returnsRentWhenFoodDisabled() {
        OccupancyEntity occupancy = OccupancyEntity.builder()
                .rentSnapshot(new BigDecimal("8000"))
                .foodEnabled(false)
                .foodChargeSnapshot(new BigDecimal("2500"))
                .build();

        assertThat(OccupancyBillingCalculator.computeMonthlyExpected(occupancy))
                .isEqualByComparingTo(new BigDecimal("8000"));
    }

    @Test
    void computeMonthlyExpected_addsFoodChargeWhenFoodEnabled() {
        OccupancyEntity occupancy = OccupancyEntity.builder()
                .rentSnapshot(new BigDecimal("8000"))
                .foodEnabled(true)
                .foodIncludedInRent(false)
                .foodChargeSnapshot(new BigDecimal("2500"))
                .build();

        assertThat(OccupancyBillingCalculator.computeMonthlyExpected(occupancy))
                .isEqualByComparingTo(new BigDecimal("10500"));
    }

    @Test
    void computeMonthlyExpected_treatsMissingFoodChargeAsZero() {
        OccupancyEntity occupancy = OccupancyEntity.builder()
                .rentSnapshot(new BigDecimal("8000"))
                .foodEnabled(true)
                .foodIncludedInRent(false)
                .build();

        assertThat(OccupancyBillingCalculator.computeMonthlyExpected(occupancy))
                .isEqualByComparingTo(new BigDecimal("8000"));
    }
}
