package com.gym.gymmembershipsystem.util;

public class PasswordHashGenerator {

    public static void main(String[] args) {

        String password =
                "Admin123!";

        String hashedPassword =
                PasswordUtil.hashPassword(
                        password
                );

        System.out.println(
                "PASSWORD:"
        );

        System.out.println(
                password
        );

        System.out.println();

        System.out.println(
                "HASH:"
        );

        System.out.println(
                hashedPassword
        );
    }
}