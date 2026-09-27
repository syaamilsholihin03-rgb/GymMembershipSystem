package com.gym.gymmembershipsystem.util;

import java.sql.Connection;

public class TestDatabase {

    public static void main(String[] args) {

        try {
            Connection connection = DatabaseConnection.getConnection();

            System.out.println("DATABASE CONNECTED SUCCESSFULLY!");

            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}