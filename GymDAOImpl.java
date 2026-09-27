package com.gym.gymmembershipsystem.dao;

import com.gym.gymmembershipsystem.model.GymPackage;
import com.gym.gymmembershipsystem.model.GymService;
import com.gym.gymmembershipsystem.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class GymDAOImpl implements GymDAO {

    @Override
    public List<GymPackage> getAllPackages()
            throws SQLException {

        List<GymPackage> packages =
                new ArrayList<>();

        String sql =
                "SELECT package_id, "
                + "package_name, "
                + "monthly_price, "
                + "sauna_included, "
                + "locker_included, "
                + "personal_training_included "
                + "FROM packages "
                + "ORDER BY package_id";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                GymPackage gymPackage =
                        new GymPackage();

                gymPackage.setPackageId(
                        resultSet.getInt(
                                "package_id"
                        )
                );

                gymPackage.setPackageName(
                        resultSet.getString(
                                "package_name"
                        )
                );

                gymPackage.setMonthlyPrice(
                        resultSet.getBigDecimal(
                                "monthly_price"
                        )
                );

                gymPackage.setSaunaIncluded(
                        resultSet.getBoolean(
                                "sauna_included"
                        )
                );

                gymPackage.setLockerIncluded(
                        resultSet.getBoolean(
                                "locker_included"
                        )
                );

                gymPackage.setPersonalTrainingIncluded(
                        resultSet.getBoolean(
                                "personal_training_included"
                        )
                );

                packages.add(gymPackage);
            }
        }

        return packages;
    }


    @Override
    public List<GymService> getAllServices()
            throws SQLException {

        List<GymService> services =
                new ArrayList<>();

        String sql =
                "SELECT service_id, "
                + "service_name, "
                + "price "
                + "FROM services "
                + "ORDER BY service_id";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

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
                                "price"
                        )
                );

                services.add(service);
            }
        }

        return services;
    }
}