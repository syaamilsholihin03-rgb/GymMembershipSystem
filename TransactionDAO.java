package com.gym.gymmembershipsystem.dao;

import com.gym.gymmembershipsystem.model.GymPackage;
import com.gym.gymmembershipsystem.model.GymService;
import com.gym.gymmembershipsystem.model.GymTransaction;
import com.gym.gymmembershipsystem.model.Member;
import com.gym.gymmembershipsystem.model.TransactionDetail;
import com.gym.gymmembershipsystem.model.TransactionRecord;

import java.sql.SQLException;
import java.util.List;

public interface TransactionDAO {

    // CREATE - MEMBERSHIP
    boolean saveMembership(
            Member member,
            GymPackage gymPackage,
            GymTransaction transaction,
            List<GymService> selectedServices,
            int personalTrainingSessions
    ) throws SQLException;


    // CREATE - WALK-IN
    boolean saveWalkIn(
            GymTransaction transaction,
            List<GymService> selectedServices,
            int personalTrainingSessions
    ) throws SQLException;


    // READ - ALL
    List<TransactionRecord> getAllTransactions()
            throws SQLException;


    // SEARCH
    List<TransactionRecord> searchTransactions(
            String searchTerm,
            String customerType
    ) throws SQLException;


    // READ - ONE
    TransactionRecord getTransactionById(
            int transactionId
    ) throws SQLException;


    // READ - FULL DETAILS
    TransactionDetail getTransactionDetailById(
            int transactionId
    ) throws SQLException;


    // UPDATE
    boolean updateTransaction(
            TransactionDetail transactionDetail
    ) throws SQLException;


    // DELETE
    boolean deleteTransaction(
            int transactionId
    ) throws SQLException;
}