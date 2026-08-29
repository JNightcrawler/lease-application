package com.leaseapp.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.OffsetDateTime;

public final class CostCalculator {

    private CostCalculator() {
    }

    /**
     * Calculates rental cost. Any part of a day in progress counts as a full
     * day (minimum of 1 day) - the common convention for daily rentals.
     * Change the rounding here if you'd rather bill hourly or prorate.
     */
    public static BigDecimal calculate(BigDecimal costPerDay, int quantity, OffsetDateTime from, OffsetDateTime to) {
        long hours = Duration.between(from, to).toHours();
        long days = Math.max(1, (long) Math.ceil(hours / 24.0));

        return costPerDay
                .multiply(BigDecimal.valueOf(quantity))
                .multiply(BigDecimal.valueOf(days))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
