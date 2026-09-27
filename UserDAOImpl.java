package com.gym.gymmembershipsystem.dao;

import com.gym.gymmembershipsystem.model.User;
import com.gym.gymmembershipsystem.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAOImpl implements UserDAO {

    @Override
    public User findByUsername(String username)
            throws SQLException {

        String sql =
                "SELECT user_id, username, password_hash, full_name, role "
                + "FROM users "
                + "WHERE username = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, username);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    User user = new User();

                    user.setUserId(
                            resultSet.getInt("user_id")
                    );

                    user.setUsername(
                            resultSet.getString("username")
                    );

                    user.setPasswordHash(
                            resultSet.getString("password_hash")
                    );

                    user.setFullName(
                            resultSet.getString("full_name")
                    );

                    user.setRole(
                            resultSet.getString("role")
                    );

                    return user;
                }
            }
        }

        return null;
    }
}