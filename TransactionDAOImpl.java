package com.gym.gymmembershipsystem.dao;

import com.gym.gymmembershipsystem.model.GymPackage;
import com.gym.gymmembershipsystem.model.GymService;
import com.gym.gymmembershipsystem.model.GymTransaction;
import com.gym.gymmembershipsystem.model.Member;
import com.gym.gymmembershipsystem.model.TransactionDetail;
import com.gym.gymmembershipsystem.model.TransactionRecord;
import com.gym.gymmembershipsystem.util.DatabaseConnection;

import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImpl implements TransactionDAO {


    // =========================================================
    // CREATE MEMBERSHIP
    // =========================================================
    @Override
    public boolean saveMembership(
            Member member,
            GymPackage gymPackage,
            GymTransaction transaction,
            List<GymService> selectedServices,
            int personalTrainingSessions)
            throws SQLException {

        Connection connection = null;

        try {

            connection =
                    DatabaseConnection.getConnection();

            connection.setAutoCommit(false);


            int memberId =
                    insertMember(
                            connection,
                            member
                    );


            transaction.setMemberId(
                    memberId
            );


            int transactionId =
                    insertTransaction(
                            connection,
                            transaction
                    );


            if (selectedServices != null
                    && !selectedServices.isEmpty()) {

                insertServices(
                        connection,
                        transactionId,
                        selectedServices
                );
            }


            connection.commit();

            return true;

        } catch (SQLException e) {

            rollback(connection);

            throw e;

        } finally {

            closeConnection(connection);
        }
    }


    // =========================================================
    // CREATE WALK-IN
    // =========================================================
    @Override
    public boolean saveWalkIn(
            GymTransaction transaction,
            List<GymService> selectedServices,
            int personalTrainingSessions)
            throws SQLException {

        Connection connection = null;

        try {

            connection =
                    DatabaseConnection.getConnection();

            connection.setAutoCommit(false);


            int transactionId =
                    insertTransaction(
                            connection,
                            transaction
                    );


            if (selectedServices != null
                    && !selectedServices.isEmpty()) {

                insertServices(
                        connection,
                        transactionId,
                        selectedServices
                );
            }


            connection.commit();

            return true;

        } catch (SQLException e) {

            rollback(connection);

            throw e;

        } finally {

            closeConnection(connection);
        }
    }


    // =========================================================
    // INSERT MEMBER
    // =========================================================
    private int insertMember(
            Connection connection,
            Member member)
            throws SQLException {

        String sql =
                "INSERT INTO members "
                + "(full_name, phone, email, gender, registration_date) "
                + "VALUES (?, ?, ?, ?, ?)";


        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS
                     )) {


            statement.setString(
                    1,
                    member.getFullName()
            );

            statement.setString(
                    2,
                    member.getPhone()
            );

            statement.setString(
                    3,
                    member.getEmail()
            );

            statement.setString(
                    4,
                    member.getGender()
            );

            statement.setDate(
                    5,
                    java.sql.Date.valueOf(
                            member.getRegistrationDate()
                    )
            );


            statement.executeUpdate();


            try (ResultSet resultSet =
                         statement.getGeneratedKeys()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }


        throw new SQLException(
                "Unable to create member."
        );
    }


    // =========================================================
    // INSERT TRANSACTION
    // =========================================================
    private int insertTransaction(
            Connection connection,
            GymTransaction transaction)
            throws SQLException {

        String sql =
                "INSERT INTO transactions "
                + "(customer_type, "
                + "member_id, "
                + "customer_name, "
                + "package_id, "
                + "number_of_months, "
                + "walk_in_fee, "
                + "total_amount) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";


        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS
                     )) {


            statement.setString(
                    1,
                    transaction.getCustomerType()
            );


            if (transaction.getMemberId() != null) {

                statement.setInt(
                        2,
                        transaction.getMemberId()
                );

            } else {

                statement.setNull(
                        2,
                        Types.INTEGER
                );
            }


            statement.setString(
                    3,
                    transaction.getCustomerName()
            );


            if (transaction.getPackageId() != null) {

                statement.setInt(
                        4,
                        transaction.getPackageId()
                );

            } else {

                statement.setNull(
                        4,
                        Types.INTEGER
                );
            }


            statement.setInt(
                    5,
                    transaction.getNumberOfMonths()
            );


            statement.setBigDecimal(
                    6,
                    transaction.getWalkInFee()
            );


            statement.setBigDecimal(
                    7,
                    transaction.getTotalAmount()
            );


            statement.executeUpdate();


            try (ResultSet resultSet =
                         statement.getGeneratedKeys()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }


        throw new SQLException(
                "Unable to create transaction."
        );
    }


    // =========================================================
    // INSERT SERVICES
    // =========================================================
    private void insertServices(
            Connection connection,
            int transactionId,
            List<GymService> selectedServices)
            throws SQLException {

        String sql =
                "INSERT INTO transaction_services "
                + "(transaction_id, service_id, quantity, unit_price) "
                + "VALUES (?, ?, ?, ?)";


        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {


            for (GymService service : selectedServices) {


                statement.setInt(
                        1,
                        transactionId
                );


                statement.setInt(
                        2,
                        service.getServiceId()
                );


                statement.setInt(
                        3,
                        1
                );


                statement.setBigDecimal(
                        4,
                        service.getPrice()
                );


                statement.addBatch();
            }


            statement.executeBatch();
        }
    }


    // =========================================================
    // READ ALL TRANSACTIONS
    // =========================================================
    @Override
    public List<TransactionRecord> getAllTransactions()
            throws SQLException {

        List<TransactionRecord> transactions =
                new ArrayList<>();


        String sql =
                "SELECT "
                + "t.transaction_id, "
                + "t.customer_name, "
                + "t.customer_type, "
                + "p.package_name, "
                + "t.number_of_months, "
                + "t.walk_in_fee, "
                + "t.transaction_date, "
                + "t.total_amount "
                + "FROM transactions t "
                + "LEFT JOIN packages p "
                + "ON t.package_id = p.package_id "
                + "ORDER BY t.transaction_id DESC";


        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {


            while (resultSet.next()) {

                transactions.add(
                        mapTransactionRecord(
                                resultSet
                        )
                );
            }
        }


        return transactions;
    }


    // =========================================================
    // SEARCH
    // =========================================================
    @Override
    public List<TransactionRecord> searchTransactions(
            String searchTerm,
            String customerType)
            throws SQLException {

        List<TransactionRecord> transactions =
                new ArrayList<>();


        StringBuilder sql =
                new StringBuilder();


        sql.append(
                "SELECT "
                + "t.transaction_id, "
                + "t.customer_name, "
                + "t.customer_type, "
                + "p.package_name, "
                + "t.number_of_months, "
                + "t.walk_in_fee, "
                + "t.transaction_date, "
                + "t.total_amount "
                + "FROM transactions t "
                + "LEFT JOIN packages p "
                + "ON t.package_id = p.package_id "
                + "WHERE 1 = 1 "
        );


        List<Object> parameters =
                new ArrayList<>();


        if (searchTerm != null
                && !searchTerm.trim().isEmpty()) {

            String cleanSearch =
                    searchTerm.trim();


            sql.append(
                    "AND ("
                    + "LOWER(t.customer_name) LIKE LOWER(?) "
                    + "OR CAST(t.transaction_id AS CHAR) LIKE ?"
                    + ") "
            );


            parameters.add(
                    "%" + cleanSearch + "%"
            );

            parameters.add(
                    "%" + cleanSearch + "%"
            );
        }


        if (customerType != null
                && !customerType.trim().isEmpty()) {

            sql.append(
                    "AND t.customer_type = ? "
            );

            parameters.add(
                    customerType.trim()
            );
        }


        sql.append(
                "ORDER BY t.transaction_id DESC"
        );


        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql.toString()
                        )
        ) {


            for (int i = 0;
                 i < parameters.size();
                 i++) {

                statement.setObject(
                        i + 1,
                        parameters.get(i)
                );
            }


            try (ResultSet resultSet =
                         statement.executeQuery()) {


                while (resultSet.next()) {

                    transactions.add(
                            mapTransactionRecord(
                                    resultSet
                            )
                    );
                }
            }
        }


        return transactions;
    }


    // =========================================================
    // READ ONE SIMPLE RECORD
    // =========================================================
    @Override
    public TransactionRecord getTransactionById(
            int transactionId)
            throws SQLException {

        String sql =
                "SELECT "
                + "t.transaction_id, "
                + "t.customer_name, "
                + "t.customer_type, "
                + "p.package_name, "
                + "t.number_of_months, "
                + "t.walk_in_fee, "
                + "t.transaction_date, "
                + "t.total_amount "
                + "FROM transactions t "
                + "LEFT JOIN packages p "
                + "ON t.package_id = p.package_id "
                + "WHERE t.transaction_id = ?";


        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {


            statement.setInt(
                    1,
                    transactionId
            );


            try (ResultSet resultSet =
                         statement.executeQuery()) {


                if (resultSet.next()) {

                    return mapTransactionRecord(
                            resultSet
                    );
                }
            }
        }


        return null;
    }


    // =========================================================
    // READ FULL TRANSACTION DETAILS
    // =========================================================
    @Override
    public TransactionDetail getTransactionDetailById(
            int transactionId)
            throws SQLException {

        String sql =
                "SELECT "
                + "t.transaction_id, "
                + "t.customer_name, "
                + "t.customer_type, "
                + "t.package_id, "
                + "t.number_of_months, "
                + "t.walk_in_fee, "
                + "t.transaction_date, "
                + "t.total_amount, "
                + "m.phone, "
                + "m.email, "
                + "m.gender, "
                + "p.package_name, "
                + "p.personal_training_included "
                + "FROM transactions t "
                + "LEFT JOIN members m "
                + "ON t.member_id = m.member_id "
                + "LEFT JOIN packages p "
                + "ON t.package_id = p.package_id "
                + "WHERE t.transaction_id = ?";


        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {


            statement.setInt(
                    1,
                    transactionId
            );


            try (ResultSet resultSet =
                         statement.executeQuery()) {


                if (!resultSet.next()) {
                    return null;
                }


                TransactionDetail detail =
                        new TransactionDetail();


                detail.setTransactionId(
                        resultSet.getInt(
                                "transaction_id"
                        )
                );


                detail.setCustomerName(
                        resultSet.getString(
                                "customer_name"
                        )
                );


                detail.setCustomerType(
                        resultSet.getString(
                                "customer_type"
                        )
                );


                detail.setPhone(
                        resultSet.getString(
                                "phone"
                        )
                );


                detail.setEmail(
                        resultSet.getString(
                                "email"
                        )
                );


                detail.setGender(
                        resultSet.getString(
                                "gender"
                        )
                );


                int packageId =
                        resultSet.getInt(
                                "package_id"
                        );


                if (resultSet.wasNull()) {

                    detail.setPackageId(
                            null
                    );

                } else {

                    detail.setPackageId(
                            packageId
                    );
                }


                String packageName =
                        resultSet.getString(
                                "package_name"
                        );


                if (packageName == null
                        || packageName.trim().isEmpty()) {

                    packageName = "-";
                }


                detail.setPackageName(
                        packageName
                );


                detail.setNumberOfMonths(
                        resultSet.getInt(
                                "number_of_months"
                        )
                );


                detail.setPersonalTrainingIncluded(
                        resultSet.getBoolean(
                                "personal_training_included"
                        )
                );


                detail.setWalkInFee(
                        resultSet.getBigDecimal(
                                "walk_in_fee"
                        )
                );


                java.sql.Timestamp timestamp =
                        resultSet.getTimestamp(
                                "transaction_date"
                        );


                if (timestamp != null) {

                    detail.setTransactionDate(
                            timestamp.toLocalDateTime()
                    );
                }


                detail.setTotalAmount(
                        resultSet.getBigDecimal(
                                "total_amount"
                        )
                );


                if ("WALK_IN".equals(
                        detail.getCustomerType())) {

                    detail.setServices(
                            getTransactionServices(
                                    connection,
                                    transactionId
                            )
                    );
                }


                return detail;
            }
        }
    }


    // =========================================================
    // READ WALK-IN SERVICES
    // =========================================================
    private List<GymService> getTransactionServices(
            Connection connection,
            int transactionId)
            throws SQLException {

        List<GymService> services =
                new ArrayList<>();


        String sql =
                "SELECT "
                + "s.service_id, "
                + "s.service_name, "
                + "ts.unit_price "
                + "FROM transaction_services ts "
                + "INNER JOIN services s "
                + "ON ts.service_id = s.service_id "
                + "WHERE ts.transaction_id = ? "
                + "ORDER BY s.service_id";


        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {


            statement.setInt(
                    1,
                    transactionId
            );


            try (ResultSet resultSet =
                         statement.executeQuery()) {


                while (resultSet.next()) {


                    GymService service =
                            new GymService();


                    service.setServiceId(
                            resultSet.getInt(
                                    "service_id"
                            )
                    );


                    service.setServiceName(
                            resultSet.getString(
                                    "service_name"
                            )
                    );


                    service.setPrice(
                            resultSet.getBigDecimal(
                                    "unit_price"
                            )
                    );


                    services.add(
                            service
                    );
                }
            }
        }


        return services;
    }


    // =========================================================
    // UPDATE TRANSACTION
    // =========================================================
    @Override
    public boolean updateTransaction(
            TransactionDetail detail)
            throws SQLException {

        Connection connection = null;

        try {

            connection =
                    DatabaseConnection.getConnection();

            connection.setAutoCommit(false);


            Integer memberId =
                    getMemberIdForTransaction(
                            connection,
                            detail.getTransactionId()
                    );


            int affectedRows;


            // =================================================
            // UPDATE MEMBERSHIP
            // =================================================
            if ("MEMBER".equals(
                    detail.getCustomerType())) {


                if (memberId == null) {

                    throw new SQLException(
                            "Member record not found for this transaction."
                    );
                }


                if (detail.getPackageId() == null) {

                    throw new SQLException(
                            "Membership package is required."
                    );
                }


                updateMember(
                        connection,
                        memberId,
                        detail
                );


                deleteTransactionServices(
                        connection,
                        detail.getTransactionId()
                );


                String sql =
                        "UPDATE transactions "
                        + "SET customer_name = ?, "
                        + "package_id = ?, "
                        + "number_of_months = ?, "
                        + "walk_in_fee = ?, "
                        + "total_amount = ? "
                        + "WHERE transaction_id = ? "
                        + "AND customer_type = 'MEMBER'";


                try (PreparedStatement statement =
                             connection.prepareStatement(sql)) {


                    statement.setString(
                            1,
                            detail.getCustomerName()
                    );


                    statement.setInt(
                            2,
                            detail.getPackageId()
                    );


                    statement.setInt(
                            3,
                            detail.getNumberOfMonths()
                    );


                    statement.setBigDecimal(
                            4,
                            BigDecimal.ZERO
                    );


                    statement.setBigDecimal(
                            5,
                            detail.getTotalAmount()
                    );


                    statement.setInt(
                            6,
                            detail.getTransactionId()
                    );


                    affectedRows =
                            statement.executeUpdate();
                }
            }


            // =================================================
            // UPDATE WALK-IN
            // =================================================
            else if ("WALK_IN".equals(
                    detail.getCustomerType())) {


                deleteTransactionServices(
                        connection,
                        detail.getTransactionId()
                );


                String sql =
                        "UPDATE transactions "
                        + "SET customer_name = ?, "
                        + "package_id = NULL, "
                        + "number_of_months = 0, "
                        + "walk_in_fee = ?, "
                        + "total_amount = ? "
                        + "WHERE transaction_id = ? "
                        + "AND customer_type = 'WALK_IN'";


                try (PreparedStatement statement =
                             connection.prepareStatement(sql)) {


                    statement.setString(
                            1,
                            detail.getCustomerName()
                    );


                    statement.setBigDecimal(
                            2,
                            detail.getWalkInFee()
                    );


                    statement.setBigDecimal(
                            3,
                            detail.getTotalAmount()
                    );


                    statement.setInt(
                            4,
                            detail.getTransactionId()
                    );


                    affectedRows =
                            statement.executeUpdate();
                }


                if (affectedRows > 0
                        && detail.getServices() != null
                        && !detail.getServices().isEmpty()) {


                    insertServices(
                            connection,
                            detail.getTransactionId(),
                            detail.getServices()
                    );
                }
            }


            else {

                throw new SQLException(
                        "Invalid customer type."
                );
            }


            if (affectedRows <= 0) {

                connection.rollback();

                return false;
            }


            connection.commit();

            return true;

        } catch (SQLException e) {

            rollback(connection);

            throw e;

        } finally {

            closeConnection(connection);
        }
    }


    // =========================================================
    // UPDATE MEMBER INFORMATION
    // =========================================================
    private void updateMember(
            Connection connection,
            int memberId,
            TransactionDetail detail)
            throws SQLException {

        String sql =
                "UPDATE members "
                + "SET full_name = ?, "
                + "phone = ?, "
                + "email = ?, "
                + "gender = ? "
                + "WHERE member_id = ?";


        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {


            statement.setString(
                    1,
                    detail.getCustomerName()
            );


            statement.setString(
                    2,
                    detail.getPhone()
            );


            statement.setString(
                    3,
                    detail.getEmail()
            );


            statement.setString(
                    4,
                    detail.getGender()
            );


            statement.setInt(
                    5,
                    memberId
            );


            statement.executeUpdate();
        }
    }


    // =========================================================
    // DELETE TRANSACTION
    // =========================================================
    @Override
    public boolean deleteTransaction(
            int transactionId)
            throws SQLException {

        Connection connection = null;

        try {

            connection =
                    DatabaseConnection.getConnection();

            connection.setAutoCommit(false);


            Integer memberId =
                    getMemberIdForTransaction(
                            connection,
                            transactionId
                    );


            deleteTransactionServices(
                    connection,
                    transactionId
            );


            String sql =
                    "DELETE FROM transactions "
                    + "WHERE transaction_id = ?";


            int affectedRows;


            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {


                statement.setInt(
                        1,
                        transactionId
                );


                affectedRows =
                        statement.executeUpdate();
            }


            if (affectedRows > 0
                    && memberId != null) {

                deleteMemberIfUnused(
                        connection,
                        memberId
                );
            }


            connection.commit();

            return affectedRows > 0;

        } catch (SQLException e) {

            rollback(connection);

            throw e;

        } finally {

            closeConnection(connection);
        }
    }


    // =========================================================
    // GET MEMBER ID
    // =========================================================
    private Integer getMemberIdForTransaction(
            Connection connection,
            int transactionId)
            throws SQLException {

        String sql =
                "SELECT member_id "
                + "FROM transactions "
                + "WHERE transaction_id = ?";


        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {


            statement.setInt(
                    1,
                    transactionId
            );


            try (ResultSet resultSet =
                         statement.executeQuery()) {


                if (resultSet.next()) {


                    int memberId =
                            resultSet.getInt(
                                    "member_id"
                            );


                    if (!resultSet.wasNull()) {

                        return memberId;
                    }
                }
            }
        }


        return null;
    }


    // =========================================================
    // DELETE SERVICES
    // =========================================================
    private void deleteTransactionServices(
            Connection connection,
            int transactionId)
            throws SQLException {

        String sql =
                "DELETE FROM transaction_services "
                + "WHERE transaction_id = ?";


        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {


            statement.setInt(
                    1,
                    transactionId
            );


            statement.executeUpdate();
        }
    }


    // =========================================================
    // DELETE MEMBER IF UNUSED
    // =========================================================
    private void deleteMemberIfUnused(
            Connection connection,
            int memberId)
            throws SQLException {

        String checkSql =
                "SELECT COUNT(*) "
                + "FROM transactions "
                + "WHERE member_id = ?";


        try (PreparedStatement statement =
                     connection.prepareStatement(
                             checkSql
                     )) {


            statement.setInt(
                    1,
                    memberId
            );


            try (ResultSet resultSet =
                         statement.executeQuery()) {


                if (resultSet.next()
                        && resultSet.getInt(1) == 0) {


                    String deleteSql =
                            "DELETE FROM members "
                            + "WHERE member_id = ?";


                    try (PreparedStatement deleteStatement =
                                 connection.prepareStatement(
                                         deleteSql
                                 )) {


                        deleteStatement.setInt(
                                1,
                                memberId
                        );


                        deleteStatement.executeUpdate();
                    }
                }
            }
        }
    }


    // =========================================================
    // MAP TRANSACTION RECORD
    // =========================================================
    private TransactionRecord mapTransactionRecord(
            ResultSet resultSet)
            throws SQLException {

        TransactionRecord record =
                new TransactionRecord();


        record.setTransactionId(
                resultSet.getInt(
                        "transaction_id"
                )
        );


        record.setCustomerName(
                resultSet.getString(
                        "customer_name"
                )
        );


        record.setCustomerType(
                resultSet.getString(
                        "customer_type"
                )
        );


        String packageName =
                resultSet.getString(
                        "package_name"
                );


        if (packageName == null
                || packageName.trim().isEmpty()) {

            packageName = "-";
        }


        record.setPackageName(
                packageName
        );


        record.setNumberOfMonths(
                resultSet.getInt(
                        "number_of_months"
                )
        );


        record.setWalkInFee(
                resultSet.getBigDecimal(
                        "walk_in_fee"
                )
        );


        java.sql.Timestamp timestamp =
                resultSet.getTimestamp(
                        "transaction_date"
                );


        if (timestamp != null) {

            record.setTransactionDate(
                    timestamp.toLocalDateTime()
            );
        }


        record.setTotalAmount(
                resultSet.getBigDecimal(
                        "total_amount"
                )
        );


        return record;
    }


    // =========================================================
    // ROLLBACK
    // =========================================================
    private void rollback(
            Connection connection) {

        if (connection != null) {

            try {

                connection.rollback();

            } catch (SQLException e) {

                e.printStackTrace();
            }
        }
    }


    // =========================================================
    // CLOSE CONNECTION
    // =========================================================
    private void closeConnection(
            Connection connection) {

        if (connection != null) {

            try {

                connection.setAutoCommit(true);
                connection.close();

            } catch (SQLException e) {

                e.printStackTrace();
            }
        }
    }
}