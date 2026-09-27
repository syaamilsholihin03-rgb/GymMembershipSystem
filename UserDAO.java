package com.gym.gymmembershipsystem.dao;

import com.gym.gymmembershipsystem.model.User;

import java.sql.SQLException;

public interface UserDAO {

    User findByUsername(
            String username
    ) throws SQLException;
}