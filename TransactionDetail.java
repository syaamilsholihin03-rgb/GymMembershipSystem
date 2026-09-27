package com.gym.gymmembershipsystem.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDetail {

    private int transactionId;

    private String customerName;

    private String customerType;

    private String phone;

    private String email;

    private String gender;

    private Integer packageId;

    private String packageName;

    private int numberOfMonths;

    private boolean personalTrainingIncluded;

    private BigDecimal walkInFee;

    private LocalDateTime transactionDate;

    private BigDecimal totalAmount;

    private List<GymService> services =
            new ArrayList<>();


    public TransactionDetail() {
    }


    public int getTransactionId() {
        return transactionId;
    }


    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }


    public String getCustomerName() {
        return customerName;
    }


    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }


    public String getCustomerType() {
        return customerType;
    }


    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }


    public String getPhone() {
        return phone;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getGender() {
        return gender;
    }


    public void setGender(String gender) {
        this.gender = gender;
    }


    public Integer getPackageId() {
        return packageId;
    }


    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }


    public String getPackageName() {
        return packageName;
    }


    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }


    public int getNumberOfMonths() {
        return numberOfMonths;
    }


    public void setNumberOfMonths(int numberOfMonths) {
        this.numberOfMonths = numberOfMonths;
    }


    public boolean isPersonalTrainingIncluded() {
        return personalTrainingIncluded;
    }


    public void setPersonalTrainingIncluded(
            boolean personalTrainingIncluded) {

        this.personalTrainingIncluded =
                personalTrainingIncluded;
    }


    public BigDecimal getWalkInFee() {
        return walkInFee;
    }


    public void setWalkInFee(BigDecimal walkInFee) {
        this.walkInFee = walkInFee;
    }


    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }


    public void setTransactionDate(
            LocalDateTime transactionDate) {

        this.transactionDate =
                transactionDate;
    }


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }


    public void setTotalAmount(
            BigDecimal totalAmount) {

        this.totalAmount =
                totalAmount;
    }


    public List<GymService> getServices() {
        return services;
    }


    public void setServices(
            List<GymService> services) {

        if (services == null) {

            this.services =
                    new ArrayList<>();

        } else {

            this.services =
                    services;
        }
    }


    public int getFreePersonalTrainingSessions() {

        if (!personalTrainingIncluded) {
            return 0;
        }

        return numberOfMonths;
    }
}