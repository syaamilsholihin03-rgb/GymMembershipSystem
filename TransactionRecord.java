package com.gym.gymmembershipsystem.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionRecord {

    private int transactionId;

    private String customerName;

    private String customerType;

    private String packageName;

    private int numberOfMonths;

    private BigDecimal walkInFee;

    private LocalDateTime transactionDate;

    private BigDecimal totalAmount;


    public TransactionRecord() {
    }


    public TransactionRecord(
            int transactionId,
            String customerName,
            String customerType,
            String packageName,
            int numberOfMonths,
            BigDecimal walkInFee,
            LocalDateTime transactionDate,
            BigDecimal totalAmount) {

        this.transactionId = transactionId;
        this.customerName = customerName;
        this.customerType = customerType;
        this.packageName = packageName;
        this.numberOfMonths = numberOfMonths;
        this.walkInFee = walkInFee;
        this.transactionDate = transactionDate;
        this.totalAmount = totalAmount;
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