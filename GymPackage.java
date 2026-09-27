package com.gym.gymmembershipsystem.model;

import java.math.BigDecimal;

public class GymPackage {

    private int packageId;
    private String packageName;
    private BigDecimal monthlyPrice;

    private boolean saunaIncluded;
    private boolean lockerIncluded;
    private boolean personalTrainingIncluded;

    public GymPackage() {
    }

    public GymPackage(
            int packageId,
            String packageName,
            BigDecimal monthlyPrice,
            boolean saunaIncluded,
            boolean lockerIncluded,
            boolean personalTrainingIncluded) {

        this.packageId = packageId;
        this.packageName = packageName;
        this.monthlyPrice = monthlyPrice;
        this.saunaIncluded = saunaIncluded;
        this.lockerIncluded = lockerIncluded;
        this.personalTrainingIncluded = personalTrainingIncluded;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public BigDecimal getMonthlyPrice() {
        return monthlyPrice;
    }

    public void setMonthlyPrice(BigDecimal monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    public boolean isSaunaIncluded() {
        return saunaIncluded;
    }

    public void setSaunaIncluded(boolean saunaIncluded) {
        this.saunaIncluded = saunaIncluded;
    }

    public boolean isLockerIncluded() {
        return lockerIncluded;
    }

    public void setLockerIncluded(boolean lockerIncluded) {
        this.lockerIncluded = lockerIncluded;
    }

    public boolean isPersonalTrainingIncluded() {
        return personalTrainingIncluded;
    }

    public void setPersonalTrainingIncluded(
            boolean personalTrainingIncluded) {

        this.personalTrainingIncluded =
                personalTrainingIncluded;
    }
}