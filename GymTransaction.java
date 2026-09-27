package com.gym.gymmembershipsystem.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GymTransaction {

    private int transactionId;
    private String customerType;

    private Integer memberId;
    private String customerName;

    private Integer packageId;
    private int numberOfMonths;

    private BigDecimal walkInFee;
    private LocalDateTime transactionDate;
    private BigDecimal totalAmount;

    public GymTransaction() {
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    public int getNumberOfMonths() {
        return numberOfMonths;
    }

    public void setNumberOfMonths(int numberOfMonths) {
        this.numberOfMonths = numberOfMonths;
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

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}