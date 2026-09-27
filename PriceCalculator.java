package com.gym.gymmembershipsystem.service;

import com.gym.gymmembershipsystem.model.GymPackage;
import com.gym.gymmembershipsystem.model.GymService;

import java.math.BigDecimal;
import java.util.List;

public class PriceCalculator {

    /*
     * Walk-In base fee:
     * RM7.00 per visit
     */
    private static final BigDecimal WALK_IN_FEE =
            new BigDecimal("7.00");


    /*
     * MEMBERSHIP
     *
     * Total =
     * Monthly Package Price x Number of Months
     */
    public BigDecimal calculateMembership(
            GymPackage gymPackage,
            int numberOfMonths) {

        if (gymPackage == null || numberOfMonths <= 0) {
            return BigDecimal.ZERO;
        }

        return gymPackage
                .getMonthlyPrice()
                .multiply(
                        BigDecimal.valueOf(numberOfMonths)
                );
    }


    /*
     * WALK-IN
     *
     * Base Fee = RM7
     *
     * Optional services:
     * Sauna         = RM10
     * Locker Rental = RM5
     */
    public BigDecimal calculateWalkIn(
            List<GymService> selectedServices) {

        BigDecimal total = WALK_IN_FEE;

        if (selectedServices == null) {
            return total;
        }

        for (GymService service : selectedServices) {

            total = total.add(
                    service.getPrice()
            );
        }

        return total;
    }


    public BigDecimal getWalkInFee() {
        return WALK_IN_FEE;
    }
}