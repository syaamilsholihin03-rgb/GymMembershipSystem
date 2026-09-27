package com.gym.gymmembershipsystem.dao;

import com.gym.gymmembershipsystem.model.GymPackage;
import com.gym.gymmembershipsystem.model.GymService;
import java.sql.SQLException;
import java.util.List;

public interface GymDAO {

    List<GymPackage> getAllPackages() throws SQLException;

    List<GymService> getAllServices() throws SQLException;
}